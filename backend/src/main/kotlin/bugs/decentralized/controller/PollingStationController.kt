package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.repository.PollingStationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Used during elections for voting
 */
@RestController
@RequestMapping("/polling_station")
class PollingStationController @Autowired constructor(
    private val pollingStationRepository: PollingStationRepository
) {

    @PutMapping("/vote_permission/{accountAddress}")
    fun sendVotePermission(
        @PathVariable accountAddress: AccountAddress,
        @RequestBody votePermission: TransactionData.VotePermission,
    ): Transaction {

        // TOOD Validate polling station? idk
        return Transaction.create(
            sender = BlockchainApplication.KEYS.publicAccount.toAddress(),
            receiver = accountAddress,
            data = TransactionData(
                votePermission = votePermission
            ),
            keyPair = BlockchainApplication.KEYS,
            nonce = 0UL, // TODO Count the number of transactions we have made
        )
    }
}
