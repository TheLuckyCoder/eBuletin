package bugs.decentralized.model.information

import bugs.decentralized.utils.StringMap
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class DriverLicense(
    val lastName: String,
    val firstName: String,
    val placeAndDateOfBirth: String,
    val issueDate: LocalDate,
    val expirationDate: LocalDate,
    val issuedBy: String,
    val licenseNumber: String,
    val validFrom: LocalDate,
    val validUntil: LocalDate,
    val categories: List<String>
) {

    fun toMap(): StringMap = buildMap {
        put(DriverLicense::lastName.name, lastName)
        put(DriverLicense::firstName.name, firstName)
        put(DriverLicense::placeAndDateOfBirth.name, placeAndDateOfBirth)
        put(DriverLicense::issueDate.name, Json.encodeToString(issueDate))
        put(DriverLicense::expirationDate.name, Json.encodeToString(expirationDate))
        put(DriverLicense::licenseNumber.name, licenseNumber)
        put(DriverLicense::validFrom.name, Json.encodeToString(validFrom))
        put(DriverLicense::validUntil.name, Json.encodeToString(validUntil))
        put(DriverLicense::categories.name, Json.encodeToString(categories))
    }

    companion object {

        @Throws(NumberFormatException::class, SerializationException::class)
        fun fromMap(map: StringMap) = DriverLicense(
            lastName = map["lastName"]!!,
            firstName = map["firstName"]!!,
            placeAndDateOfBirth = map["placeAndDateOfBirth"]!!,
            issueDate = Json.decodeFromString(map["issueDate"]!!),
            expirationDate = Json.decodeFromString(map["expirationDate"]!!),
            issuedBy = map["issuedBy"]!!,
            licenseNumber = map["licenseNumber"]!!,
            validFrom = Json.decodeFromString(map["validFrom"]!!),
            validUntil = Json.decodeFromString(map["validUntil"]!!),
            categories = Json.decodeFromString(map["categories"]!!),
        )
    }
}
