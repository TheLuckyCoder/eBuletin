package bugs.decentralized.repository

import org.springframework.data.mongodb.repository.MongoRepository

interface PollingStationRepository : MongoRepository<String, Short>{}