package bugs.decentralized

import bugs.decentralized.model.SimpleNode
import bugs.decentralized.utils.ecdsa.ECIES
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication {
    companion object {
        val KEYS = ECIES.generateEcKeyPair() // Should be loaded from a file

        val NODE = SimpleNode(
            KEYS.getPublicHex(false),
            System.getenv("BLOCKCHAIN_SERVER_URL")
        )
    }
}

fun main(args: Array<String>) {
    runApplication<BlockchainApplication>(*args)
}
