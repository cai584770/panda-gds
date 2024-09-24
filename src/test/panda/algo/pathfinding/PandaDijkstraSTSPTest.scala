package panda.algo.pathfinding

import org.cai.algoconfig.path.{PandaDijkstraSingleSourceShortestConfig, PandaDijkstraSourceTargetShortestConfig}
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
import scala.collection.mutable.ListBuffer

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

  @Test
  def stsp1Test: Unit = {
    val results: util.Set[PathResult] = PandaDijkstraSourceTargetShortestConfig.dijkstraSourceTargetShortest(hg, 1L, 4L)

    val resultListBuffer = ListBuffer[LynxValue]()
    results.forEach { s =>
      val ids = s.nodeIds()
      val count = s.nodeIds().length
      val pathResultListBuffer = ListBuffer[LynxValue]()
      for (cursor <- 0 until count) {
        val id: Long = ids(cursor)
        pathResultListBuffer += LynxValue(nodeResult(id.toInt).values.toList)
      }
      resultListBuffer += LynxValue(pathResultListBuffer.toList)
    }
    LynxValue(resultListBuffer.toList)

    println(LynxValue(resultListBuffer.toList))

  }

}
