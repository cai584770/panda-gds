package org.cai.algoconfig.path

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{ShortestPathDijkstraStreamConfig, ShortestPathDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/7/23 9:46
 * @Version
 */
object PandaDijkstraSourceTargetShortestConfig {

  def dijkstraSourceTargetShortest(
        hugeGraph: HugeGraph,
        source: Long,
        target: Long,
        concurrency: Int = 4,
        trackRelationships: Boolean = false,
        heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction],
        progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
        terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
      ): util.Set[PathResult] = {
    val builder: ShortestPathDijkstraStreamConfigImpl.Builder = ShortestPathDijkstraStreamConfigImpl.builder.concurrency(concurrency)

    val config: ShortestPathDijkstraStreamConfig = builder.sourceNode(source).targetNode(target).build

    Dijkstra.sourceTarget(hugeGraph, config, trackRelationships, heuristicFunction, progressTracker, terminationFlag).compute.pathSet
  }

}
