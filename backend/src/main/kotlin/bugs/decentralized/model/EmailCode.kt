package bugs.decentralized.model

data class EmailCode(
    val address: AccountAddress,
    val email: String,
    val secretCode: Int,
    val expirationTimestamp: Long,
) {

    fun isValid() = expirationTimestamp < System.currentTimeMillis()
}
