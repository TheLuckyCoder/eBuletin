package bugs.decentralized.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class IdCard(
    val cnp: UInt,
    val lastName: String,
    val firstName: String,
    val address: String,
    val birthLocation: String,
    val birthDate: LocalDate,
    val sex: Char,
    val issuedBy: String,
    val series: String,
    val number: UInt,
    val validity: LocalTime,
)