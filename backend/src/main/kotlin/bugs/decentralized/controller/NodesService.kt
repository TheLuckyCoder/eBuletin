package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.SimpleNode
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.model.information.IdCard
import kotlinx.datetime.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.getForEntity
import org.springframework.web.client.postForEntity
import java.net.URI
import java.time.Duration

@Service
class NodesService @Autowired constructor(restTemplateBuilder: RestTemplateBuilder) {

    private val restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(30))
        .setReadTimeout(Duration.ofSeconds(30))
        .build()

    fun nodeIsAlive(nodeUrl: String): Boolean {
        val response = restTemplate.getForEntity<String>(URI.create("$nodeUrl/ping"))

        return response.statusCode == HttpStatus.OK
    }

    fun sendAllNodes(nodeUrl: String, allNodes: List<SimpleNode>) {
        val uri = URI.create("${nodeUrl}/nodes/${BlockchainApplication.NODE.address}")
        restTemplate.postForEntity<List<SimpleNode>>(uri, allNodes)
    }

    fun sendTransaction(nodeUrl: String, transaction: Transaction): Boolean {
        return restTemplate.postForEntity<Unit>("$nodeUrl/government/submit_transaction", transaction).statusCode == HttpStatus.OK
    }

    /**
     * THIS IS USED JUST FOR DEBUGGING!
     */
    fun submitTransaction() {
        val address = PublicAccountKey("042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c").toAddress()
        
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

        sendTransaction("https://server.aaconsl.com/blockchain"/*"http://127.0.0.1:11225"*/, transaction)
    }
}
