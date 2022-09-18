package bugs.decentralized.repository

import bugs.decentralized.model.Node
import org.springframework.data.mongodb.repository.MongoRepository

interface NodesRepository : MongoRepository<Node, String>
