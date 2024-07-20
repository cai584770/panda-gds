package panda.algo

import com.google.gson.annotations.Until
import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{ShortestPathDijkstraStreamConfig, ShortestPathDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/7/20 13:11
 * @Version
 */
class PandaDijkstraTest {
  private val dbPath = "/home/cjw/dijkstra.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("TYPE"))


  @Test
  def shortestPathDijkstraStreamConfigImpl:Unit={
    val builder: ShortestPathDijkstraStreamConfigImpl.Builder = ShortestPathDijkstraStreamConfigImpl.builder.concurrency(1)

    val config: ShortestPathDijkstraStreamConfig = builder.sourceNode(hg.toOriginalNodeId(1L)).targetNode(hg.toOriginalNodeId(5L)).build
    val set: util.Set[PathResult] = Dijkstra.sourceTarget(hg, config, false, Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE).compute.pathSet

    println(set)

  }


}
