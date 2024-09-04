package org.cai.algoconfig.path

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{AllShortestPathsDijkstraStreamConfig, AllShortestPathsDijkstraStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/7/23 9:45
 * @Version
 */
object PandaDijkstraSingleSourceShortestConfig {

  def dijkstraSingleSourceShortest(
            hugeGraph: HugeGraph,
            source: Long,
            concurrency: Int = 1,
            trackRelationships: Boolean = false,
            heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction],
            progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
            terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
          ): util.Set[PathResult] = {

    val builder: AllShortestPathsDijkstraStreamConfigImpl.Builder = AllShortestPathsDijkstraStreamConfigImpl.builder.concurrency(concurrency)
    val config: AllShortestPathsDijkstraStreamConfig = builder.sourceNode(source).build

    Dijkstra.singleSource(hugeGraph, config, trackRelationships, heuristicFunction, progressTracker, terminationFlag).compute().pathSet()
  }


}
