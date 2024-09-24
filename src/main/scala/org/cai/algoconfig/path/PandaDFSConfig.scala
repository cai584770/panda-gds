package org.cai.algoconfig.path

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, DFS, DfsBaseConfig, ExitPredicate, OneHopAggregator, TargetExitPredicate}

import java.util
import java.util.{Arrays, List => JList}

/**
 * @author cai584770
 * @date 2024/7/23 9:44
 * @Version
 */
object PandaDFSConfig {

  def DFS(hugeGraph: HugeGraph,
          source:Long,
          targetList:List[Long],
          aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION,
          maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH,
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
         ): Array[Long] = {
    val javaList: JList[java.lang.Long] = util.Arrays.asList(targetList.map(Long.box): _*)
    val predicate: TargetExitPredicate = new TargetExitPredicate(javaList)

    new DFS(hugeGraph,
      source,
      predicate,
      aggregatorFunction,
      maxDepth,
      progressTracker).compute.toArray
  }

}
