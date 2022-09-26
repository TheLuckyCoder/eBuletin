package bugs.decentralized.model

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Serializable
@Document
data class PollingStation(
    @Id
    val id: Long,
    val name: String,
)
