package bugs.decentralized.repository

import bugs.decentralized.model.Transaction
import java.util.*

object TransactionsRepository {

    // This includes all valid transactions that haven't been added to a block yet
    val transactionsPool = Collections.synchronizedList(mutableListOf<Transaction>())

    fun getTransaction(): List<Transaction> {
        return synchronized(transactionsPool) {
            transactionsPool.toList()
        }
    }


}
