package bugs.decentralized.model.information

import bugs.decentralized.utils.StringMap
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class MedicalCard(
    val lastName: String,
    val firstName: String,
    val insuranceCode: UInt, // 20 length
    val documentNumber: UInt,
    val expiryDate: LocalDate,
) {

    companion object {
        @Throws(NumberFormatException::class)
        fun fromMap(map: StringMap) = MedicalCard(
            lastName = map["lastName"]!!,
            firstName = map["firstName"]!!,
            insuranceCode = map["insuranceCode"]!!.toUInt(),
            documentNumber = map["documentNumber"]!!.toUInt(),
            expiryDate = Json.decodeFromString(map["expiryDate"]!!),
        )
    }
}
