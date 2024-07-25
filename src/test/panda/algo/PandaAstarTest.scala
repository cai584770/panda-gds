package panda.algo

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.astar.AStar
import org.neo4j.gds.paths.astar.config.{ShortestPathAStarStreamConfig, ShortestPathAStarStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

/**
 * @author cai584770
 * @date 2024/7/20 15:38
 * @Version
 */
class PandaAstarTest {

  private val dbPath = "/home/cjw/bfs.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("REL"))

  @Test
  def astar():Unit={
    val builder = ShortestPathAStarStreamConfigImpl.builder.concurrency(1)

    val config: ShortestPathAStarStreamConfig = builder.sourceNode(hg.toOriginalNodeId(1L))
      .targetNode(hg.toOriginalNodeId(3L))
      .build()

    val result: PathResult = AStar.sourceTarget(hg, config, ProgressTracker.NULL_TRACKER,TerminationFlag.RUNNING_TRUE).compute.findFirst.get

    println(result)

  }

}
