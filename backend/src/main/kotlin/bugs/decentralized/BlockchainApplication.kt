package bugs.decentralized

import bugs.decentralized.model.Block
import bugs.decentralized.model.SimpleNode
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.utils.ecdsa.ECIES
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication {
    companion object {
        val DOTENV = dotenv()

        val GENESIS_BLOCK = Block(0UL, 0L, emptyList(), "GENESIS", 0UL)

        val KEYS = ECIES.generateEcKeyPair() // Should be loaded from a file

        val NODE = SimpleNode(
            KEYS.getPublicHex(false),
            DOTENV.get("BLOCKCHAIN_SERVER_URL")
        )
    }
}

fun main(args: Array<String>) {
    val applicationContext = runApplication<BlockchainApplication>(*args)
    val blockRepository = applicationContext.getBean<BlockRepository>()

    if (blockRepository.count() == 0L) {
        blockRepository.insert(BlockchainApplication.GENESIS_BLOCK)
    }
}
