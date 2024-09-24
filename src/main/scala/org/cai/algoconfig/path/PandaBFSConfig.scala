package org.cai.algoconfig.path

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, ExitPredicate, OneHopAggregator, TargetExitPredicate}

import java.util
import java.util.{Arrays, List => JList}

/**
 * @author cai584770
 * @date 2024/7/23 9:45
 * @Version
 */
object PandaBFSConfig {

  def BFS(hugeGraph: HugeGraph,
          source: Long,
          targetList:List[Long],
          concurrency: Int = 4,
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
          maximumDepth: Long = org.neo4j.gds.paths.traverse.BFS.ALL_DEPTHS_ALLOWED
         ): Array[Long] = {
    val javaList: JList[java.lang.Long] = util.Arrays.asList(targetList.map(Long.box): _*)
    val predicate: TargetExitPredicate = new TargetExitPredicate(javaList)

    val aggregator: OneHopAggregator = new OneHopAggregator()

    org.neo4j.gds.paths.traverse.BFS.create(
      hugeGraph,
      source,
      predicate,
      aggregator,
      concurrency,
      progressTracker,
      maximumDepth
    ).compute().toArray

  }

}
