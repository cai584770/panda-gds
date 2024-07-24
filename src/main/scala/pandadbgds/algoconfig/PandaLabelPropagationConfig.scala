package pandadbgds.algoconfig

import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.labelpropagation.{LabelPropagation, LabelPropagationStreamConfigImpl}

import java.util.concurrent.ExecutorService

/**
 * @author cai584770
 * @date 2024/7/15 12:26
 * @Version
 */
object PandaLabelPropagationConfig {

  def labelPropagation(
                        hugeGraph: HugeGraph,
                        concurrency: Int = 1,
                        maxIterations: Int = 10,
                        nodeWeightProperty: String = null,
                        executorService: ExecutorService = DefaultPool.INSTANCE,
                        progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
                      ): Array[Long] = {
    val config: LabelPropagationStreamConfigImpl.Builder = new LabelPropagationStreamConfigImpl.Builder
    config.concurrency(concurrency).maxIterations(maxIterations).nodeWeightProperty(nodeWeightProperty)
    new LabelPropagation(hugeGraph, config.build(), executorService, progressTracker).compute().labels.toArray

  }


}
