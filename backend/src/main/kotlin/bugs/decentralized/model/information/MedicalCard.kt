package bugs.decentralized.model.information

import bugs.decentralized.utils.StringMap
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class MedicalCard(
    val lastName: String,
    val firstName: String,
    val insuranceCode: String, // 20 length
    val documentNumber: Long, // 16 length
    val expiryDate: LocalDate,
) {

    fun toMap(): StringMap = buildMap {
        put(MedicalCard::lastName.name, lastName)
        put(MedicalCard::firstName.name, firstName)
        put(MedicalCard::insuranceCode.name, insuranceCode.toString())
        put(MedicalCard::documentNumber.name, documentNumber.toString())
        put(MedicalCard::expiryDate.name, expiryDate.toString())
    }

    companion object {
        @Throws(NumberFormatException::class, SerializationException::class)
        fun fromMap(map: StringMap) = MedicalCard(
            lastName = map["lastName"]!!,
            firstName = map["firstName"]!!,
            insuranceCode = map["insuranceCode"]!!,
            documentNumber = map["documentNumber"]!!.toLong(),
            expiryDate = Json.decodeFromString(map["expiryDate"]!!),
        )
    }
}
