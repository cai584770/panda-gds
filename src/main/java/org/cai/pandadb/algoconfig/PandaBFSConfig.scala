package org.cai.pandadb.algoconfig

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, BFS, ExitPredicate}
import org.neo4j.gds.paths.traverse.ExitPredicate.Result

/**
 * @author cai584770
 * @date 2024/7/23 9:45
 * @Version
 */
object PandaBFSConfig {

  def BFS(hugeGraph: HugeGraph,
          source: Long,
          exitPredicate: ExitPredicate,
          aggregatorFunction: Aggregator,
          concurrency: Int = 1,
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
          maximumDepth: Long = org.neo4j.gds.paths.traverse.BFS.ALL_DEPTHS_ALLOWED
         ):  Array[Long] = {

    org.neo4j.gds.paths.traverse.BFS.create(
      hugeGraph,
      source,
      exitPredicate,
      aggregatorFunction,
      concurrency,
      progressTracker,
      maximumDepth
    ).compute().toArray

  }

}
