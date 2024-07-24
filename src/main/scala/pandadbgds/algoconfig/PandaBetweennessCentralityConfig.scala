package pandadbgds.algoconfig

import org.neo4j.gds.betweenness.{BetweennessCentrality, ForwardTraverser, FullSelectionStrategy, SelectionStrategy}
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker

import java.util.concurrent.ExecutorService

/**
 * @author cai584770
 * @date 2024/7/20 10:43
 * @Version
 */
object PandaBetweennessCentralityConfig {

  def betweennessCentrality(
                             hugeGraph: HugeGraph,
                             selectionStrategy: SelectionStrategy = new FullSelectionStrategy,
                             traverserFactory: ForwardTraverser.Factory = ForwardTraverser.Factory.unweighted,
                             executorService: ExecutorService = DefaultPool.INSTANCE,
                             concurrency: Int = 1,
                             progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
                           ): HugeAtomicDoubleArray = {

    new BetweennessCentrality(hugeGraph, selectionStrategy, traverserFactory, executorService, concurrency, progressTracker).compute.centralities

  }


}
