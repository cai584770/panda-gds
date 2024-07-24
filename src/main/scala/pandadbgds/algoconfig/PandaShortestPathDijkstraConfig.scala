package pandadbgds.algoconfig

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{ShortestPathDijkstraStreamConfig, ShortestPathDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util.Optional
import java.util

/**
 * @author cai584770
 * @date 2024/7/20 15:31
 * @Version
 */
object PandaShortestPathDijkstraConfig {

  def dijkstra(hugeGraph: HugeGraph,
               sourceNodeId: Long,
               targetNodeId: Long,
               concurrency:Int = 1,
               trackRelationShips: Boolean = false,
               heuristicFunction: Optional[Dijkstra.HeuristicFunction] =Optional.empty[Dijkstra.HeuristicFunction],
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
               terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
              ): util.Set[PathResult] = {
    val builder: ShortestPathDijkstraStreamConfigImpl.Builder = ShortestPathDijkstraStreamConfigImpl.builder.concurrency(concurrency)

    val config: ShortestPathDijkstraStreamConfig = builder.sourceNode(sourceNodeId).targetNode(targetNodeId).build
    val result: util.Set[PathResult] = Dijkstra.sourceTarget(hugeGraph, config, trackRelationShips, heuristicFunction, progressTracker, terminationFlag).compute.pathSet

    result
  }

}
