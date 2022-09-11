package bugs.decentralized.controller

import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Transaction
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.utils.RSA
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Used to communicate with the other validators in the network
 */
@RestController
class ValidatorController @Autowired constructor(
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
) {

    @GetMapping("ping")
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

    // Show all transactions in the transaction pool.
    /*@GetMapping("/transactions")
    fun transactions(): List<Transaction> {
        return transactionsRepository.findAll()
    }*/

    @PostMapping("/transactions")
    fun newTransaction(@RequestBody transaction: Transaction) {
        // TODO for Andrei: validate the transaction and add it to a pool

        val decryptedHash = RSA.decryptString(transaction.signature, )

        val computedHash = transaction.hash

        if(decryptedHash == computedHash)
        {

        }
    }

    @GetMapping("/nodes")
    fun nodes(): List<Node> {
        return nodesRepository.findAll()
    }

    @PutMapping("/node")
    fun node(@RequestBody node: Node) {
        nodesRepository.save(node)
    }
}
