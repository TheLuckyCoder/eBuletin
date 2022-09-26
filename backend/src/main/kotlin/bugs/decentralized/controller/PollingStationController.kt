package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.repository.PollingStationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Used during elections for voting
 */
@RestController
@RequestMapping("/polling_station")
class PollingStationController @Autowired constructor(
    private val pollingStationRepository: PollingStationRepository
) {
    /*fun vote(
        votePermission: TransactionData.VotePermission,
        pollingStation: PollingStation,
        candidate: String?,
        party: String
    ): Transaction {
        check(votePermission.electionYear.toInt() == LocalDateTime.now().year) {
            "Invalid year"
        }

        check(!pollingStationRepository.findById(pollingStation.ID).isEmpty) {
            "Invalid Polling Station"
        }

        val receiver = PublicAccountKey(
            "042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c"
        ).toAddress()

        return Transaction.create(
            BlockchainApplication.KEYS.publicAccount.toAddress(),
            receiver,
            data = TransactionData(
                vote = TransactionData.Vote(
                    candidate = candidate,
                    party = party,
                    votePermission = votePermission
                )
            ),
            keyPair = BlockchainApplication.KEYS,
            nonce = 0UL,
        )
    }*/

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
