package bugs.decentralized.blockchain

import bugs.decentralized.utils.SHA

data class Transaction(
    val sender: String,
    val receiver: String,
    val signature: String,
    val data: String,
    val nonce: ULong
) {
    val hash = SHA.sha256(receiver + signature + data + nonce)
}
