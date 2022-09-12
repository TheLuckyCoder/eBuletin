package bugs.decentralized

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.security.Security

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication

fun main(args: Array<String>) {
    runApplication<BlockchainApplication>(*args)
}
