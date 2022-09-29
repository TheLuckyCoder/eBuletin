package bugs.decentralized.blockchain

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.BlockchainApplication.Companion.GENESIS_BLOCK
import bugs.decentralized.controller.service.NodesService
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.SHA
import kotlinx.coroutines.*
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.event.AfterSaveCallback
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.milliseconds

@Component
class Blockchain @Autowired constructor(
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
    private val nodesService: NodesService,
    blockListener: BlockListener,
) {

    @Component
    class BlockListener @Autowired constructor() : AfterSaveCallback<Block> {

        var callback: (() -> Unit)? = null

        override fun onAfterSave(entity: Block, document: Document, collection: String): Block {
            callback?.invoke()

            return entity
        }
    }

    private val log = LoggerExtensions.getLogger<Blockchain>()
    private val transactionRepository = TransactionsRepository

    init {
        blockListener.callback = {
            miningSessionJob?.cancel()
        }
    }

    private var miningSessionJob: Job? = null

    /**
     * Generate [Poet.computeWaitTime]
     * Wait the generated time and propose a block
     * If another valid block is proposed the waiting is cancelled
     * save
     * Repeat
     **/
    suspend fun miningSession(currentNode: Node) = coroutineScope {
        while (transactionRepository.getTransaction().isEmpty()) {
            delay(10)
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        miningSessionJob = launch {
            val lastBlock = blockRepository.findAll().maxBy { it.blockNumber }
            //compute waitTime and wait:
            val waitTime = Poet.computeWaitTime(lastBlock, currentNode.address)
            log.info("Waiting for ${waitTime.milliseconds} to mine a block")
            Thread.sleep(waitTime / 2)
            ensureActive()
            Thread.sleep(waitTime / 2)
            ensureActive()

            //generate the new block to be proposed
            val transactions = synchronized(transactionRepository.transactionsPool) {
                val result = transactionRepository.transactionsPool.toList()
                transactionRepository.transactionsPool.clear()
                result
            }
            require(transactions.isNotEmpty()) { "No transactions to mine" }
            val newBlock = Poet.generateBlock(
                transactions, lastBlock, currentNode, SHA.sha256Hex(lastBlock.stateHash + lastBlock.hash)
            )
            log.info("Mined a new block! $newBlock")

            require(newBlock.stateHash != null) { "General Hash can not be null" }

            ensureActive()
            launch(Dispatchers.IO) {
                blockRepository.save(newBlock)
            }

            nodesRepository.findAll().forEach {
                if (it != BlockchainApplication.NODE) {
                    launch(Dispatchers.IO) {
                        nodesService.sendBlock(it.url, newBlock)
                    }
                }
            }
        }

        try {
            miningSessionJob?.join()
        } finally {
        }
    }

    companion object {
        fun verify(blocks: List<Block>) {
            check(blocks.isNotEmpty()) { "Blockchain can't be empty" }
            check(blocks[0] == GENESIS_BLOCK) { "Invalid first block!" }

            for (i in 1 until blocks.size) {
                val current = blocks[i]
                val previous = blocks[i - 1]

                check(current.blockNumber == i.toLong()) { "Invalid block number ${current.blockNumber} for block #$i!" }

                check(current.parentHash == previous.hash) { "Invalid previous block hash for block #$i!" }

                check(
                    current.timestamp > previous.timestamp + Poet.computeWaitTime(
                        previous,
                        previous.nodeAddress
                    )
                ) { "Invalid waitTime for block #$i!" }
            }
        }
    }
}
