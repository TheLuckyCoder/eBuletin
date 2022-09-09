package bugs.decentralized.repository

import bugs.decentralized.model.Block
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.TransactionData
import org.springframework.data.mongodb.repository.MongoRepository

interface BlockRepository : MongoRepository<Block, ULong>

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
