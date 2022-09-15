package bugs.decentralized.utils.ecdsa

import bugs.decentralized.model.PublicAccountKey

data class SimpleKeyPair(
    val private: String,
    val public: PublicAccountKey
)
