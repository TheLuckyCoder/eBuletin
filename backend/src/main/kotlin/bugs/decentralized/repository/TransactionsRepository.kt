package bugs.decentralized.repository

import bugs.decentralized.model.Transaction
import java.util.*

object TransactionsRepository {

    // These includ all valid transactions that haven't been added to a block yet
    val transactionsPool: MutableList<Transaction> = Collections.synchronizedList(emptyList())
}
