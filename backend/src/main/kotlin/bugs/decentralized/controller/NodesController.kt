package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.LoggerExtensions
import bugs.decentralized.utils.ecdsa.Sign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bouncycastle.util.encoders.Hex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.SignatureException

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
    fun ping(): String {
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
        log.info("Received new transaction")
        val hash = transaction.hash

        if (transactionsRepository.getTransaction().any { it.hash == hash }) {
            log.warn("A transaction that already is in the pool has been received")
            return ResponseEntity.badRequest()
                .body("A transaction that already is in the pool has been received")
        }

        val blocks = blockRepository.findAll()
        val transactionInBlocks = blocks.any { block ->
            block.transactions.any { it.hash == hash }
        }

        if (transactionInBlocks) {
            log.error("A transaction that already is in the blockchain has been received")
            return ResponseEntity.badRequest()
                .body("A transaction that already is in the blockchain has been received")
        }

        val receiverPublicAccountKey = try {
            val publicKey = Sign.signedMessageToKey(Json.encodeToString(transaction.data), transaction.signature)
            PublicAccountKey(Hex.toHexString(publicKey.toByteArray()))
        } catch (e: SignatureException) {
            log.error("Transaction has an invalid signature")
            return ResponseEntity.badRequest().body("Transaction has an invalid signature")
        }

        val toAddress = receiverPublicAccountKey.toAddress()
        if (transaction.sender != toAddress) {
            log.error("Transaction's senders address and signature don't match")
            return ResponseEntity.badRequest()
                .body("Transaction's senders address and signature don't match")
        }

        transactionsRepository.transactionsPool.add(transaction)

        return ResponseEntity.accepted().build()
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
                        } else if (nodesService.nodeIsAlive(node.url)) { // Only add active nodes to the database
                            nodesRepository.save(node)
                            nodesService.sendAllNodes(node.url, nodesRepository.findAll())
                        }

                        log.info("Added new Node($node)")
                    }
                }
            }
            .toList()
            .joinAll()
    }
}