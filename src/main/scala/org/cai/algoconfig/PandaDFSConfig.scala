package org.cai.algoconfig

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.ExitPredicate.Result
import org.neo4j.gds.paths.traverse.{Aggregator, DFS, DfsBaseConfig, ExitPredicate}

/**
 * @author cai584770
 * @date 2024/7/23 9:44
 * @Version
 */
object PandaDFSConfig {

  def DFS(hugeGraph: HugeGraph,
          exitPredicate: ExitPredicate,
          aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION,
          maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH,
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
         ): Array[Long] = {
    new DFS(hugeGraph,
      hugeGraph.toMappedNodeId(1L),
      (s: Long, t: Long, w: Double) => Result.FOLLOW,
      aggregatorFunction,
      maxDepth,
      progressTracker).compute.toArray
  }

}
