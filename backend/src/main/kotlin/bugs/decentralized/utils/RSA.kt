package bugs.decentralized.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.Key
import javax.crypto.Cipher

object RSA
{

    fun encryptString(data: String, publicKey: Key): String {
        val encryptCipher: Cipher = Cipher.getInstance("RSA")
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return encryptCipher.doFinal(data.toByteArray())!!.toHexString()
    }

    inline fun <reified T> encrypt(data: T, publicKey: Key): String =
        encryptString(Json.encodeToString(data), publicKey)

    fun decryptString(data: String, key: Key) : String
    {
        val decrypter: Cipher = Cipher.getInstance("RSA")
        decrypter.init(Cipher.DECRYPT_MODE, key)
        return decrypter.doFinal(data.toByteArray())!!.toHexString()
    }
}
