package org.cai.pandadb.algoconfig

import org.neo4j.gds.api.IdMap
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.labelpropagation.{LabelPropagation, LabelPropagationStreamConfigImpl}

import scala.collection.mutable

/**
 * @author cai584770
 * @date 2024/7/15 12:26
 * @Version
 */
object LabelPropagationConfig {

  def defaultLabelPropagation(hugeGraph: HugeGraph):Array[Long]={
    val config: LabelPropagationStreamConfigImpl.Builder = new LabelPropagationStreamConfigImpl.Builder

    config.concurrency(1).maxIterations(10).nodeWeightProperty(null)

    val propagation: Array[Long] = new LabelPropagation(hugeGraph, config.build(), DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute().labels.toArray

    propagation
  }

}
