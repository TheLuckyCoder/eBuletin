package bugs.decentralized

import bugs.decentralized.blockchain.Blockchain
import bugs.decentralized.controller.NodesService
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.Sign
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.io.File
import java.math.BigInteger

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication {
    companion object {
        val DOTENV = dotenv()

        val GENESIS_BLOCK = Block(0L, 0L, emptyList(), "GENESIS", "0x0")

        val KEYS = generateKeys() // Should be loaded from a file

        val NODE = Node(
            KEYS.publicAccount.toAddress().value,
            DOTENV.get("BLOCKCHAIN_SERVER_URL"),
        )

        private fun generateKeys(): SimpleKeyPair {
            val privateFile = File("private.key")
            val publicFile = File("public.key")

            return try {
                val private = privateFile.readBytes()
                val public = publicFile.readBytes()

                SimpleKeyPair(private, public)
            } catch (e: Exception) {
                ECIES.generateEcKeyPair().also { keys ->
                    privateFile.writeBytes(keys.privateBinary)
                    publicFile.writeBytes(keys.publicBinary)
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun main(args: Array<String>) {
    val applicationContext = runApplication<BlockchainApplication>(*args)
    val blockRepository = applicationContext.getBean<BlockRepository>()
    val nodesRepository = applicationContext.getBean<NodesRepository>()

    if (blockRepository.count() == 0L) {
        blockRepository.insert(BlockchainApplication.GENESIS_BLOCK)
    }

    if (nodesRepository.count() == 0L) {
        nodesRepository.insert(BlockchainApplication.NODE)
    }

    val blockchain: Blockchain = applicationContext.getBean()

    val nodesService: NodesService = applicationContext.getBean()

    GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            println("Starting mining session")
            blockchain.miningSession(BlockchainApplication.NODE)
        }
    }

    val private = BigInteger("1bbaee028b2141c59c92ecaef66015eaabb950e7ed5c56583175c53cfb8cf606", 16)
    val signatureData = Sign.sign("0xFD29BDCAE955514E3B34E7EE4C06729DB9CC4711", Sign.ECKeyPair.from(private))
val json = Json.encodeToString(signatureData)
    /*GlobalScope.launch {
        launch {
            delay(500)
            println("Sending nodes")
            nodesService.sendAllNodes("https://server.aaconsl.com/blockchain", nodesRepository.findAll())
            delay(500)
            println("Sending transactions")
            nodesService.submitTransaction()
        }
    }*/
    println(json)
}
