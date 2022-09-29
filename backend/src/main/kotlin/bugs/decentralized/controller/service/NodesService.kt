package bugs.decentralized.controller.service

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.model.information.IdCard
import kotlinx.datetime.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.postForObject
import java.net.URI
import java.time.Duration

@Service
class NodesService @Autowired constructor(restTemplateBuilder: RestTemplateBuilder) {

    private val restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(30))
        .setReadTimeout(Duration.ofSeconds(30))
        .build()

    fun pingNode(nodeUrl: String): Boolean {
        return try {
            val response = restTemplate.postForObject<String>("$nodeUrl/ping", BlockchainApplication.NODE)
            response == "OK"
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendAllNodes(nodeUrl: String, allNodes: List<Node>) {
        try {
            val uri = URI.create("${nodeUrl}/nodes/${BlockchainApplication.NODE.address}")
            restTemplate.put(uri, allNodes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitTransaction(nodeUrl: String, transaction: Transaction): Boolean {
        return try {
            restTemplate.put("$nodeUrl/government/submit_transaction", transaction)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendTransaction(nodeUrl: String, transaction: Transaction): Boolean {
        return try {
            restTemplate.put("$nodeUrl/transaction", transaction)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendBlock(nodeUrl: String, block: Block): Boolean {
        return try {
            restTemplate.put("$nodeUrl/block", block)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * THIS IS USED JUST FOR DEBUGGING!
     */
    fun submitTransaction() {
        val address =
            PublicAccountKey("042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c").toAddress()

        val transaction = Transaction.create(
            BlockchainApplication.KEYS.publicAccount.toAddress(),
            address,
            data = TransactionData(
                information = TransactionData.Information(
                    idCard = IdCard(
                        cnp = 1234567890123U,
                        lastName = "Filea",
                        firstName = "Razvan",
                        address = "Str.Caltun",
                        birthLocation = "Sibiu",
                        sex = 'M',
                        series = "SB",
                        seriesNumber = 123456U,
                        validity = LocalDate(2024, 5, 12),
                        issuedBy = "Politia Sibiu"
                    ).toMap()
                )
            ),
            keyPair = BlockchainApplication.KEYS,
            nonce = 0UL,
        )

        submitTransaction("https://server.aaconsl.com/blockchain", transaction)
    }
}
