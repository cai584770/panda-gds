package panda.algo.pathfinding

import org.cai.algoconfig.path.PandaDijkstraSingleSourceShortestConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{AllShortestPathsDijkstraStreamConfig, AllShortestPathsDijkstraStreamConfigImpl, ShortestPathDijkstraStreamConfig, ShortestPathDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/7/20 13:11
 * @Version
 */
class PandaDijkstraSTSPTest {
  private val dbPath = "/home/cjw/db/dstsp.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Location", "ROAD")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("ROAD"))

  @Test
  def stspTest:Unit={
    val builder: ShortestPathDijkstraStreamConfigImpl.Builder = ShortestPathDijkstraStreamConfigImpl.builder.concurrency(1)

    val config: ShortestPathDijkstraStreamConfig = builder.sourceNode(hg.toOriginalNodeId(0L)).targetNode(hg.toOriginalNodeId(5L)).build
    val set: util.Set[PathResult] = Dijkstra.sourceTarget(hg, config, false, Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE).compute.pathSet

    set.forEach{s =>
      val ids = s.nodeIds()
      val count = s.nodeIds().length
      for (i <- 0 until count){
        val id = ids(i)
        println(nodeResult(id.toInt).values + ":" + id)
      }
    }
    println(set)

  }



}
