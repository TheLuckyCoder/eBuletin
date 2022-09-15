package bugs.decentralized.controller

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.SimpleNode
import bugs.decentralized.model.Transaction
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
        return restTemplate.postForEntity<Transaction>(nodeUrl, transaction).statusCode == HttpStatus.OK
    }
}
