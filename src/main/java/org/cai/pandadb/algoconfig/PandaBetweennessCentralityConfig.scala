package org.cai.pandadb.algoconfig

import org.neo4j.gds.betweenness.{BetweennessCentrality, ForwardTraverser, FullSelectionStrategy}
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker

/**
 * @author cai584770
 * @date 2024/7/20 10:43
 * @Version
 */
object PandaBetweennessCentralityConfig {

 def betweennessCentrality(hugeGraph: HugeGraph):HugeAtomicDoubleArray={
   val centralities: HugeAtomicDoubleArray = new BetweennessCentrality(hugeGraph, new FullSelectionStrategy, ForwardTraverser.Factory.unweighted, DefaultPool.INSTANCE, 1, ProgressTracker.NULL_TRACKER).compute.centralities

   centralities
 }


}
