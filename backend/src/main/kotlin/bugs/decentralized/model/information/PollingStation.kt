package bugs.decentralized.model.information

import bugs.decentralized.utils.StringMap
import kotlinx.serialization.Serializable

@Serializable
data class PollingStation (
    val NAME: String,
    val ID: Short
){
    fun toMap(): StringMap = buildMap {
        put(PollingStation::NAME.name, NAME)
        put(PollingStation::ID.name, ID.toString())
    }
}