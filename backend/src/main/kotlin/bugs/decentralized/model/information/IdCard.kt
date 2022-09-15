package bugs.decentralized.model.information

import bugs.decentralized.utils.StringMap
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.Throws

@Serializable
data class IdCard(
    val cnp: ULong,
    val lastName: String,
    val firstName: String,
    val birthLocation: String,
    val address: String,
    val sex: Char,
    val series: String,
    val seriesNumber: UInt,
    val validity: LocalDate,
    val issuedBy: String,
) {

    fun toMap(): StringMap = buildMap {
        put(IdCard::cnp.name, cnp.toString())
        put(IdCard::lastName.name, lastName)
        put(IdCard::firstName.name, firstName)
        put(IdCard::birthLocation.name, birthLocation)
        put(IdCard::address.name, address)
        put(IdCard::sex.name, sex.toString())
        put(IdCard::series.name, series)
        put(IdCard::seriesNumber.name, seriesNumber.toString())
        put(IdCard::validity.name, Json.encodeToString(validity))
        put(IdCard::issuedBy.name, issuedBy)
    }

    companion object {
        @Throws(NumberFormatException::class, SerializationException::class)
        fun fromMap(map: StringMap) = IdCard(
            cnp = map[IdCard::cnp.name]!!.toULong(),
            lastName = map[IdCard::lastName.name]!!,
            firstName = map[IdCard::firstName.name]!!,
            address = map[IdCard::address.name]!!,
            birthLocation = map[IdCard::birthLocation.name]!!,
            sex = map[IdCard::sex.name]!![0],
            issuedBy = map[IdCard::issuedBy.name]!!,
            series = map[IdCard::series.name]!!,
            seriesNumber = map[IdCard::seriesNumber.name]!!.toUInt(),
            validity = Json.decodeFromString(IdCard::validity.name)
        )
    }
}
