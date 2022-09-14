package bugs.decentralized.model

import bugs.decentralized.utils.StringMap
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class IdCard(
    val cnp: UInt,
    val lastName: String,
    val firstName: String,
    val birthLocation: String,
    val address: String,
    val sex: Char,
    val series: String,
    val seriosNumber: UInt,
    val validity: LocalDate,
    val issuedBy: String,
) {
    companion object {
        fun fromMap(map: StringMap) = IdCard(
            cnp = map["cnp"]!!.toUInt(),
            lastName = map["lastName"]!!,
            firstName = map["firstName"]!!,
            address = map["lastName"]!!,
            birthLocation = map["birthLocation"]!!,
            sex = map["sex"]!![0],
            issuedBy = map["issuedBy"]!!,
            series = map["series"]!!,
            seriosNumber = map["number"]!!.toUInt(),
            validity = Json.decodeFromString("validity")
        )
    }
}
