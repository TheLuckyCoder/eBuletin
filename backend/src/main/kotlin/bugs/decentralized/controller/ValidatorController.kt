package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.Block
import bugs.decentralized.model.information.IdCard
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.SimpleNode
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
import kotlinx.datetime.LocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.security.SignatureException

/**
 * Used to communicate with the other validators in the network
 */
@RestController
class ValidatorController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
) {

    private val log = LoggerExtensions.getLogger<ValidatorController>()
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

    @GetMapping("/transactions")
    fun transactions(): List<Transaction> {
        return transactionsRepository.transactionsPool.toList()
    }

    @PostMapping("/transaction")
    fun newTransaction(@RequestBody transaction: Transaction): HttpStatus {
        val hash = transaction.hash

        if (transactionsRepository.transactionsPool.any { it.hash == hash }) {
            log.warn("A transaction that already is in the pool has been received")
            return HttpStatus.CONFLICT
        }

        val blocks = blockRepository.findAll()
        val transactionInBlocks = blocks.any { block ->
            block.transactions.any { it.hash == hash }
        }

        if (transactionInBlocks) {
            log.error("A transaction that already is in the blockchain has been received")
            return HttpStatus.BAD_REQUEST
        }

        val receiverPublicAccountKey = try {
            val publicKey = Sign.signedMessageToKey(Json.encodeToString(transaction.data), transaction.signature)
            PublicAccountKey(publicKey.toString())
        } catch (e: SignatureException) {
            log.error("Transaction has an invalid signature")
            return HttpStatus.BAD_REQUEST
        }

        if (transaction.sender != receiverPublicAccountKey.toAddress()) {
            log.error("Transaction's senders address and signature don't match")
            return HttpStatus.BAD_REQUEST
        }

        return HttpStatus.OK
    }

    @GetMapping("/nodes")
    fun nodes(): List<SimpleNode> {
        return nodesRepository.findAll()
    }

    @PostMapping("/nodes/{fromNodeAddress}")
    suspend fun nodes(@PathVariable fromNodeAddress: String, @RequestBody nodes: List<SimpleNode>) = coroutineScope {
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
