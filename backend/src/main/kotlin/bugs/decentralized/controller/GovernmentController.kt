package bugs.decentralized.controller

import bugs.decentralized.repository.BlockRepository
import bugs.decentralized.repository.NodesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController

/**
 * Used to communicate between a node and a local special government application
 */
@RestController
class GovernmentController @Autowired constructor(
    private val nodesService: NodesService,
    private val blockRepository: BlockRepository,
    private val nodesRepository: NodesRepository,
)
