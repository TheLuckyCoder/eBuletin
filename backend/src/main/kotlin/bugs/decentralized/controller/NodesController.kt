package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.blockchain.Poet
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.LoggerExtensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Used to communicate with the other validators in the network
 */
@RestController
class NodesController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
) {

    private val log = LoggerExtensions.getLogger<NodesController>()
    private val transactionsRepository = TransactionsRepository

    @GetMapping("/ping")
    fun ping(): String = "OK"

    @PostMapping("/ping")
    fun ping(@RequestBody node: Node): String {
        if (nodesRepository.findByIdOrNull(node.address) == null) {
            log.info("Added new Node($node)")
            nodesRepository.save(node)
        }
        return "OK"
    }

    @GetMapping("/blocks")
    fun blocks(): List<Block> =
        blockRepository.findAll()

    @GetMapping("/block/{blockNumber}")
    fun block(@PathVariable blockNumber: String): Block? {
        val blockNumberLong = blockNumber.toULong()

        return blockRepository.findByIdOrNull(blockNumberLong)
    }

    @PutMapping("/block")
    fun block(@RequestBody block: Block) {
        val allBlocks = blockRepository.findAll()
        val duration = System.currentTimeMillis() - allBlocks.last().timestamp

        if (allBlocks.none { it.blockNumber == block.blockNumber || it.hash == block.hash }
            && allBlocks.maxOf { it.blockNumber } == (block.blockNumber - 1)
            && duration > Poet.computeWaitTime(allBlocks.last(), block.nodeAddress)
        ) {

            synchronized(transactionsRepository.transactionsPool) {
                transactionsRepository.transactionsPool.removeAll(block.transactions)
            }

            log.info("Block has been received $block")
            blockRepository.save(block)
        } else {
            log.error("Block already exists or has an invalid waiting time $block")
        }
    }

    @GetMapping("/transactions")
    fun transactions(): List<Transaction> {
        return transactionsRepository.getTransaction().toList()
    }

    @PutMapping("/transaction")
    fun newTransaction(@RequestBody transaction: Transaction): ResponseEntity<String> {
        val isTransactionValid: ResponseEntity<String>? =
            Transaction.checkTransaction(transaction, transactionsRepository, log, blockRepository)

        if (isTransactionValid != null)
            return isTransactionValid

        //check vote permission if existent
        if (transaction.data.vote != null && !blocks()
                .any { block ->
                    block.transactions.any {
                        it.data.votePermission?.electionData == transaction.data.vote.electionData
                                && it.receiver == transaction.sender
                    }
                }
        ) return ResponseEntity.badRequest().body("Invalid vote permission")

        log.info("Received new transaction")

        transactionsRepository.transactionsPool.add(transaction)
        log.info("Received new transaction from other node")

        return ResponseEntity.accepted().build()
    }

    suspend fun sendTransactionToAllNodes(transaction: Transaction) = coroutineScope {
        nodes().asSequence()
            .filter { it != BlockchainApplication.NODE }
            .forEach { node ->
                launch(Dispatchers.IO) {
                    log.info("Sending transaction to $node")
                    nodesService.sendTransaction(node.url, transaction)
                }
            }

        transactionsRepository.transactionsPool.add(transaction)
    }

    @GetMapping("/nodes")
    fun nodes(): List<Node> {
        return nodesRepository.findAll()
    }

    @PutMapping("/nodes/{fromNodeAddress}")
    suspend fun nodes(@PathVariable fromNodeAddress: String, @RequestBody nodes: List<Node>) = coroutineScope {
        nodes.asSequence()
            .filterNot { it.address == BlockchainApplication.NODE.address }
            .map { node ->
                launch(Dispatchers.IO) {
                    if (nodesRepository.findByIdOrNull(node.address) == null) {
                        if (node.address == fromNodeAddress) {
                            nodesRepository.save(node)
                        } else if (nodesService.pingNode(node.url)) { // Only add active nodes to the database
                            nodesRepository.save(node)
                        }

                        nodesService.sendAllNodes(node.url, nodesRepository.findAll())
                        log.info("Added new Node($node)")
                    }
                }
            }
            .toList()
            .joinAll()
    }
}
