package panda.algo

import org.cai.algoconfig.PandaDijkstraSingleSourceShortestConfig
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

  @Test
  def dijkstraSingleSourceShortest: Unit = {
    val builder: AllShortestPathsDijkstraStreamConfigImpl.Builder = AllShortestPathsDijkstraStreamConfigImpl.builder.concurrency(1)
    val config: AllShortestPathsDijkstraStreamConfig = builder.sourceNode(hg.toOriginalNodeId(1L)).build

    println(hg.toOriginalNodeId(1L))

    val result: util.Set[PathResult] = Dijkstra.singleSource(hg,config,false,Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE).compute().pathSet()

    println(result)

  }

  @Test
  def dijkstraSSS: Unit = {
    println(hg.toOriginalNodeId(1L))

    val dijkstraSingleSourceShortestResult: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hg, hg.toOriginalNodeId(1L), 1,false, Optional.empty[Dijkstra.HeuristicFunction], ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE)

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
