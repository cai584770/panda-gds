package org.cai.pandadb.algoconfig

import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.louvain.{Louvain, LouvainResult, LouvainStreamConfig, LouvainStreamConfigImpl}
import org.neo4j.gds.termination.TerminationFlag

import java.util.concurrent.ExecutorService

/**
 * @author cai584770
 * @date 2024/7/9 17:30
 * @Version
 */
object PandaLouvainConfig {

  def defaultLouvain(hugeGraph: HugeGraph): (Array[HugeLongArray], Array[Double]) = {
    val config = LouvainStreamConfigImpl.builder.maxLevels(hugeGraph.idMap().nodeCount().toInt).maxIterations(10).tolerance(TOLERANCE_DEFAULT).includeIntermediateCommunities(true).concurrency(1).build

    val louvain = new Louvain(hugeGraph, config, config.includeIntermediateCommunities, config.maxLevels, config.maxIterations, config.tolerance, config.concurrency, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE)

    louvain.setTerminationFlag(TerminationFlag.RUNNING_TRUE)

    val compute = louvain.compute

    val dendrogram: Array[HugeLongArray] = compute.dendrogramManager.getAllDendrograms
    val modularities: Array[Double] = compute.modularities

    (dendrogram, modularities)
  }

  def louvain(hugeGraph: HugeGraph,maxLevels:Int,tolerance:Double,maxIterations:Int,includeIntermediateCommunities:Boolean,concurrency:Int,progressTracker: ProgressTracker,executorService: ExecutorService,terminationFlag: TerminationFlag):(Array[HugeLongArray],Array[Double])={
    val config = LouvainStreamConfigImpl.builder.maxLevels(maxLevels).maxIterations(maxIterations).tolerance(tolerance).includeIntermediateCommunities(includeIntermediateCommunities).concurrency(concurrency).build

    val louvain = new Louvain(hugeGraph, config, config.includeIntermediateCommunities, config.maxLevels, config.maxIterations, config.tolerance, config.concurrency, progressTracker, executorService)

    louvain.setTerminationFlag(terminationFlag)

    val compute = louvain.compute

    val dendrogram: Array[HugeLongArray] = compute.dendrogramManager.getAllDendrograms
    val modularities: Array[Double] = compute.modularities

    (dendrogram,modularities)
  }

}
