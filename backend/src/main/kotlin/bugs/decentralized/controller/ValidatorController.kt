package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.ecdsa.Sign
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * Used to communicate with the other validators in the network
 */
@RestController
class ValidatorController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
) {

    private val transactionsRepository = TransactionsRepository

    @GetMapping("/ping")
    fun ping(): String {
        return "OK"
    }

    @GetMapping("/blocks")
    fun blocks(): List<Block> {
        return blockRepository.findAll()
    }

    @GetMapping("/block/{blockNumber}")
    fun block(@PathVariable blockNumber: String): Block? {
        val blockNumberLong = blockNumber.toULong()

        return blockRepository.findByIdOrNull(blockNumberLong)
    }

    @GetMapping("/transactions")
    fun transactions(): List<Transaction> {
        return transactionsRepository.transactionsPool.toList()
    }

    @PostMapping("/transactions")
    fun newTransaction(@RequestBody transaction: Transaction): HttpStatus {
        val hash = transaction.hash

        if (transactionsRepository.transactionsPool.any { it.hash == hash }) {
            return HttpStatus.CONFLICT
        }

        val publicKey = Sign.signedMessageToKey(Json.encodeToString(transaction.data), transaction.signature)
        val publicAccountKey = PublicAccountKey(publicKey.toString())

        check(transaction.sender == publicAccountKey.toAddress())

        return HttpStatus.OK
    }

    @GetMapping("/nodes")
    fun nodes(): List<Node> {
        return nodesRepository.findAll()
    }

    @PutMapping("/nodes")
    fun nodes(@RequestBody nodes: List<Node>) {
        for (node in nodes) {
            if (nodesRepository.findByIdOrNull(node.address) == null) {
                if (nodesService.nodeIsAlive(node.url))
                    nodesRepository.save(node) // Only add active nodes to the database
            }
        }
    }
}
