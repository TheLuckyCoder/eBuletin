package bugs.decentralized.repository

import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.Block
import bugs.decentralized.model.TransactionData
import org.springframework.data.mongodb.repository.MongoRepository

interface BlockRepository : MongoRepository<Block, Long>

fun BlockRepository.getRoleOf(address: AccountAddress): String? {
    for (block in findAll()) {
        for (transaction in block.transactions) {
            if (transaction.receiver.value.equals(address.value, true)) {
                transaction.data.information?.role?.let {
                    return it
                }
            }
        }
    }

    return null
}

fun BlockRepository.getTransactionsCountBy(address: AccountAddress): ULong {
    return findAll().sumOf { block ->
        block.transactions.count { it.sender == address }.toULong()
    }
}

fun BlockRepository.getLastBlock(): Block {
    return findAll().maxBy { block ->
        block.blockNumber
    }
}

fun BlockRepository.getInformationAtAddress(
    address: AccountAddress,
    onInformationFound: (TransactionData.Information) -> Unit
) {
    val blocks = findAll()

    for (block in blocks) {
        for (transaction in block.transactions) {
            if (transaction.receiver.value.equals(address.value, true)) {
                transaction.data.information?.let {
                    onInformationFound(it)
                }
            }
        }
    }
}
