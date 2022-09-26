package bugs.decentralized.repository

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.Block
import bugs.decentralized.model.TransactionData
import org.springframework.data.mongodb.repository.MongoRepository

interface BlockRepository : MongoRepository<Block, ULong>

fun BlockRepository.getTransactionsCountBy(address: AccountAddress): Long {
    return findAll().sumOf { block ->
        block.transactions.count { it.sender == address }.toLong()
    }
}

fun BlockRepository.getInformationAtAddress(
    address: AccountAddress,
    onInformationFound: (TransactionData.Information) -> Unit
) {
    for (block in findAll()) {
        for (transaction in block.transactions) {
            if (transaction.receiver != address)
                continue

            transaction.data.information?.let {
                onInformationFound(it)
            }
        }
    }
}
