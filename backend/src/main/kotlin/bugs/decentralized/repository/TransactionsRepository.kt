package bugs.decentralized.repository

import bugs.decentralized.model.Transaction
import java.util.*

object TransactionsRepository {

    private val transactionsPool: MutableList<Transaction> = Collections.synchronizedList(emptyList())
}
