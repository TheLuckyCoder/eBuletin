package bugs.decentralized.repository

import bugs.decentralized.model.SimpleNode
import org.springframework.data.mongodb.repository.MongoRepository

interface NodesRepository : MongoRepository<SimpleNode, String>
