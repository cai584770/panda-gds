package panda.algo.pathfinding

import org.cai.algoconfig.path.PandaDijkstraSingleSourceShortestConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{AllShortestPathsDijkstraStreamConfig, AllShortestPathsDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/9/20 10:17
 * @Version
 */
class PandaDijkstraSSSPTest {
  private val dbPath = "/home/cjw/db/dstsp.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Location", "ROAD")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("ROAD"))

  @Test
  def ssspTest: Unit = {
    val builder: AllShortestPathsDijkstraStreamConfigImpl.Builder = AllShortestPathsDijkstraStreamConfigImpl.builder.concurrency(1)
    val config: AllShortestPathsDijkstraStreamConfig = builder.sourceNode(hg.toOriginalNodeId(0L)).build

    println(hg.toOriginalNodeId(0L))

    val result: util.Set[PathResult] = Dijkstra.singleSource(hg, config, false, Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE).compute().pathSet()

    result.forEach { s =>
      val ids = s.nodeIds()
      val count = s.nodeIds().length
      for (i <- 0 until count) {
        val id = ids(i)
        println(nodeResult(id.toInt).values + ":" + id)
      }
      println("--------------")
    }
    println(result)

  }

  @Test
  def dijkstraSSS: Unit = {
    println(hg.toOriginalNodeId(1L))

    val dijkstraSingleSourceShortestResult: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hg, hg.toOriginalNodeId(1L), 1, false, Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE)

    println(dijkstraSingleSourceShortestResult)

    var idsResult: Array[Array[Long]] = Array.empty[Array[Long]]

    val iterator = dijkstraSingleSourceShortestResult.iterator
    while (iterator.hasNext) {
      val dijkstraResult = iterator.next()
      idsResult = idsResult :+ dijkstraResult.nodeIds()
    }

    val result: LynxValue = LynxValue.apply(idsResult)


    println(result)

  }

}
