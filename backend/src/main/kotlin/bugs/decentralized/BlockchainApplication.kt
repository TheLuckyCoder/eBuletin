package bugs.decentralized

import bugs.decentralized.blockchain.Blockchain
import bugs.decentralized.controller.NodesController
import bugs.decentralized.controller.service.NodesService
import bugs.decentralized.model.*
import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import bugs.decentralized.repository.getLastBlock
import bugs.decentralized.utils.SHA
import bugs.decentralized.utils.ecdsa.ECIES
import bugs.decentralized.utils.ecdsa.SignatureData
import bugs.decentralized.utils.ecdsa.SimpleKeyPair
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                        byteArrayOf(
                            103,
                            16,
                            123,
                            -88,
                            28,
                            -51,
                            -82,
                            4,
                            -115,
                            126,
                            -126,
                            -46,
                            -47,
                            -32,
                            -42,
                            -36,
                            -16,
                            0,
                            -24,
                            -21,
                            -124,
                            104,
                            -26,
                            -90,
                            118,
                            -117,
                            12,
                            69,
                            -89,
                            -33,
                            -1,
                            -56
                        ),
                        byteArrayOf(
                            43,
                            42,
                            -34,
                            9,
                            83,
                            -42,
                            81,
                            119,
                            -35,
                            16,
                            104,
                            62,
                            80,
                            -38,
                            -94,
                            -17,
                            -10,
                            8,
                            111,
                            98,
                            3,
                            -8,
                            -67,
                            1,
                            7,
                            -11,
                            120,
                            87,
                            28,
                            117,
                            57,
                            30
                        )
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

    if (blockRepository.count() == 0L) {
        blockRepository.insert(BlockchainApplication.GENESIS_BLOCK)
    }

    if (nodesRepository.count() == 0L) {
        nodesRepository.insert(BlockchainApplication.NODE)
    }

    val blockchain: Blockchain = applicationContext.getBean()
    val nodesService: NodesService = applicationContext.getBean()
    val nodesController: NodesController = applicationContext.getBean()

    val nodes = nodesRepository.findAll()
    val blockList = mutableListOf<Block>()
    //Don't use this
    val nodeUrlList = mutableListOf<String>()
    val counter = mutableListOf<Int>()

    //Get the best blockchain (not the longest):
    for (node in nodes) {
        val block = nodesService.getLastBlock(node.url)

        if (!blockList.contains(block)) {
            blockList.add(block)
            nodeUrlList.add(node.url)
            counter.add(1)
        } else {
            counter[blockList.indexOf(block)]++
        }
    }

    var max = -1

    for (i in counter.indices) {
        if (counter[i] > max)
            max = counter[i]
    }

    val index = counter.indexOf(max)

    if(!blockRepository.getLastBlock().equals(blockList[index])){
        val url = nodeUrlList[index]
        blockRepository.deleteAll()
        val list = nodesService.getBlocks(url)
        blockRepository.insert(list)
    }

    GlobalScope.launch(Dispatchers.IO) {
        while (true) {
            println("Starting mining session")
            blockchain.miningSession(BlockchainApplication.NODE)
        }
    }

    /*val keyPair = SimpleKeyPair(
        "1bd963d71f8605b8fa33d3b1861e650d4525c7f51bd38b1240348ab50cfc13d0",
        "042b5e6991a99b37d8cbe752e53a13190615487834d7365045ed2acf5b637ea94940a326647d51709e8d0e71079393d2cc5815d02f48ff184271e6fa3897d3758c"
    )

    val t = Transaction.create(
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
