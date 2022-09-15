package bugs.decentralized

import bugs.decentralized.model.Block
import bugs.decentralized.model.PublicAccountKey
import bugs.decentralized.model.SimpleNode
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.io.File

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication {
    companion object {
        val DOTENV = dotenv()

        val GENESIS_BLOCK = Block(0L, 0L, emptyList(), "GENESIS", 0L)

        val KEYS = generateKeys() // Should be loaded from a file

        val NODE = SimpleNode(
            KEYS.public.value,
            DOTENV.get("BLOCKCHAIN_SERVER_URL")
        )

        private fun generateKeys(): SimpleKeyPair {
            val privateFile = File("private.key")
            val publicFile = File("public.key")

            return try {
                val private = privateFile.readText()
                val public = publicFile.readText()

                SimpleKeyPair(private, PublicAccountKey(public))
            } catch (e: Exception) {
                val keys = ECIES.generateEcKeyPair()
                val simpleKeys = SimpleKeyPair(keys.privateHex, PublicAccountKey(keys.getPublicHex(false)))
                privateFile.writeText(simpleKeys.private)
                publicFile.writeText(simpleKeys.public.value)
                simpleKeys
            }
        }
    }
}

fun main(args: Array<String>) {
    val applicationContext = runApplication<BlockchainApplication>(*args)
    val blockRepository = applicationContext.getBean<BlockRepository>()

    blockRepository.findAll()
    if (blockRepository.count() == 0L) {
        blockRepository.insert(BlockchainApplication.GENESIS_BLOCK)
    }
}
