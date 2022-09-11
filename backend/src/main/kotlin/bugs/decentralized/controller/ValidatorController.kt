package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.utils.RSA
import bugs.decentralized.repository.TransactionsRepository
import bugs.decentralized.utils.ecdsa.ECDSA
import bugs.decentralized.utils.ecdsa.ECDSA.signedMessageToKey
import bugs.decentralized.utils.ecdsa.ECDSASignature
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.math.BigInteger

/**
 * Used to communicate with the other validators in the network
 */
@RestController
class ValidatorController @Autowired constructor(
    private val nodesSerice: NodesService,
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
    fun block(@PathVariable blockNumber: String): Block {
        val blockNumberLong = blockNumber.toULong()

        return blockRepository.findById(blockNumberLong).get()
    }

    @GetMapping("/transactions")
    fun transactions(): List<Transaction> {
        return transactionsRepository.transactionsPool.toList()
    }

    @PostMapping("/transactions")
    fun newTransaction(@RequestBody transaction: Transaction): HttpStatus
    {
        val hash = transaction.hash

        if (transactionsRepository.transactionsPool.any { it.hash == hash })
        {
            return HttpStatus.CONFLICT
        }

        var isValid = true

        val publicKey = signedMessageToKey(Json.encodeToString(transaction.data), transaction.signature)

        val publicAccountKey = PublicAccountKey(publicKey.toString())

        check(transaction.sender == publicAccountKey.toAddress())

        if (isValid)
        {
            //nodesSerice.sendTransaction()
            return HttpStatus.OK
        }

        return HttpStatus.BAD_REQUEST
    }

    @GetMapping("/nodes")
    fun nodes(): List<Node> {
        return nodesRepository.findAll()
    }

    @PutMapping("/nodes")
    fun nodes(@RequestBody nodes: List<Node>) {
        for (node in nodes) {
            if (nodesRepository.findByIdOrNull(node.address) == null) {
                if (nodesSerice.doesNodeExist(node.url))
                    nodesRepository.save(node) // Only add active nodes to the database
            }
        }
    }
}
