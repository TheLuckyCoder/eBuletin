package bugs.decentralized.repository

import bugs.decentralized.blockchain.Block
import org.springframework.data.mongodb.repository.MongoRepository

interface BlockRepository : MongoRepository<Block, String> {

    fun getDataAtAddress(address: String) {
        val blocks = findAll()
        val data = HashMap<String, String>()

        for (block in blocks) {
            for (transaction in block.transactions) {
                if (transaction.receiver != address)
                    continue

                transaction.data
            }
        }
    }
}
