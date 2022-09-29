package bugs.decentralized

import bugs.decentralized.blockchain.Blockchain
import bugs.decentralized.controller.service.NodesService
import bugs.decentralized.model.AccountAddress
import bugs.decentralized.model.Block
import bugs.decentralized.model.Node
import bugs.decentralized.model.Roles
import bugs.decentralized.model.Transaction
import bugs.decentralized.model.TransactionData
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.SignatureData
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bouncycastle.util.encoders.Hex
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

        val ADMIN_ADDRESS = AccountAddress("0x1ad40f5934e44a710c3f9c8f8c69dfd0a94efd4c")
        val GENESIS_BLOCK = Block(
            0L, 0L,
            listOf(
                Transaction(
                    "efe77d75e7914b540838a45e98196aea8e81d9c9248c5b6fc7c03c56b22709e3",
                    ADMIN_ADDRESS.value,
                    ADMIN_ADDRESS.value,
                    TransactionData(TransactionData.Information(role = Roles.ADMIN)),
                    SignatureData(
                        byteArrayOf(28),
                        Hex.decode("67107ba81ccdae048d7e82d2d1e0d6dcf000e8eb8468e6a6768b0c45a7dfffc8"),
                        Hex.decode("672b2ade0953d65177dd10683e50daa2eff6086f6203f8bd0107f578571c75391e")
                    ),
                    0L
                )
            ),
            "GENESIS", "0x0",
            "0"
        )

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
    val blockchain: Blockchain = applicationContext.getBean()
    val nodesService: NodesService = applicationContext.getBean()

    if (blockRepository.count() == 0L) {
        blockRepository.insert(BlockchainApplication.GENESIS_BLOCK)
    }

    if (nodesRepository.count() == 0L) {
        nodesRepository.insert(BlockchainApplication.NODE)
    }

    val adminKeyPair = SimpleKeyPair(
        "1bd963d71f8605b8fa33d3b1861e650d4525c7f51bd38b1240348ab50cfc13d0",
        "042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c"
    )

    GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            println("Starting mining session")
            blockchain.miningSession(BlockchainApplication.NODE)
        }
    }

    val sef = SimpleKeyPair("167b302077032f483554a15b73dafe8c459b408a75acfefec44f7427899f8437", "5a8d53a870302e630b6f096c58be1efb74c00bf693dd0a55619eb2605eea0e0ec9f5200314dac550a6887cd9c9a1eb984b564e1449135cbba6cd38e91b899938")
    println(sef.publicAccount.toAddress())
    println(SHA.sha256Hex(sef.publicHex))

    val t = Transaction.create(
        sef.publicAccount.toAddress(),
        TransactionData(information = TransactionData.Information(role = Roles.GOVERNMENT, email = "razvan.filea@gmail.com")),
        adminKeyPair,
        1UL
    )

//    nodesService.submitTransaction("http://localhost:11225", t)
//    nodesService.submitTransaction("https://server.aaconsl.com/blockchain", t)


    /*val t = Transaction.create(
        keyPair.publicAccount.toAddress(),
        TransactionData(TransactionData.Information(role = Roles.ADMIN)),
        keyPair,
        0UL
    )*/


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
}
