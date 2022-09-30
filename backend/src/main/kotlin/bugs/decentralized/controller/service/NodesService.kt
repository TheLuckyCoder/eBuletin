package bugs.decentralized.controller.service

import bugs.decentralized.BlockchainApplication
import bugs.decentralized.model.*
import bugs.decentralized.model.information.IdCard
import kotlinx.datetime.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForObject
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

    fun getBlocks(nodeUrl: String): List<Block> {
        return restTemplate.getForObject("${nodeUrl}/node/blocks")
    }

    fun sendAllNodes(nodeUrl: String, allNodes: List<Node>) {
        try {
            val uri = URI.create("${nodeUrl}/node/nodes/${BlockchainApplication.NODE.address}")
            restTemplate.put(uri, allNodes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun submitTransaction(nodeUrl: String, transaction: Transaction): Boolean {
        return try {
            restTemplate.put("$nodeUrl/submit_transaction", transaction)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendTransaction(nodeUrl: String, transaction: Transaction): Boolean {
        return try {
            restTemplate.put("$nodeUrl/node/transaction", transaction)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendBlock(nodeUrl: String, block: Block): Boolean {
        return try {
            restTemplate.put("$nodeUrl/node/block", block)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun sendBlockRange(nodeUrl: String, range: List<Block>): Boolean {
        return try {
            restTemplate.put("$nodeUrl/node/blocksInRange", range)
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    fun getLastBlock(nodeUrl: String): Block {
        return restTemplate.getForObject("${nodeUrl}/node/lastBlock")
    }
}
