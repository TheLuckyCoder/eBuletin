package bugs.decentralized

import bugs.decentralized.model.SimpleNode
import bugs.decentralized.utils.ecdsa.ECIES
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.net.DatagramSocket
import java.net.InetAddress

@EnableMongoRepositories(basePackages = ["bugs.decentralized.repository"])
@SpringBootApplication
class BlockchainApplication {
    companion object {
        val keys = ECIES.generateEcKeyPair()

        val NODE = SimpleNode(
            keys.getPublicHex(false),
            ""
        )
    }
}

fun main(args: Array<String>) {
    DatagramSocket().use { socket ->
        socket.connect(InetAddress.getByName("8.8.8.8"), 10002)
        val ip = socket.remoteSocketAddress
        println(ip)

    }
    runApplication<BlockchainApplication>(*args)
}
