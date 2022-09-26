package bugs.decentralized.repository

import bugs.decentralized.model.PollingStation
import org.springframework.data.mongodb.repository.MongoRepository

interface PollingStationRepository : MongoRepository<PollingStation, Long>
