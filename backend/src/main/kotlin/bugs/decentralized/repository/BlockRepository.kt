package bugs.decentralized.repository

import bugs.decentralized.blockchain.Block
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.TransactionData
import kotlinx.serialization.json.internal.decodeStringToJsonTree
import org.springframework.data.mongodb.repository.MongoRepository

interface BlockRepository : MongoRepository<Block, String>


fun BlockRepository.getInformationAtAddress(
    address: AccountAddress,
    onInformationFound: (TransactionData.Information) -> Unit
) {
    val blocks = findAll()

    for (block in blocks) {
        for (transaction in block.transactions) {
            if (transaction.receiver != address)
                continue

            transaction.data.information?.let {
                onInformationFound(it)
            }
        }
    }
}
