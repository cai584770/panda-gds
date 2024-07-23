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

  def louvain(
               hugeGraph: HugeGraph,
               tolerance: Double = TOLERANCE_DEFAULT,
               maxIterations: Int = 10,
               includeIntermediateCommunities: Boolean = true,
               concurrency: Int = 1,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
               executorService: ExecutorService = DefaultPool.INSTANCE,
               terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
             ): (Array[HugeLongArray], Array[Double]) = {

    val config = LouvainStreamConfigImpl.builder
      .maxLevels(hugeGraph.idMap().nodeCount().toInt)
      .maxIterations(maxIterations)
      .tolerance(tolerance)
      .includeIntermediateCommunities(includeIntermediateCommunities)
      .concurrency(concurrency)
      .build

    val louvain = new Louvain(
      hugeGraph,
      config,
      config.includeIntermediateCommunities,
      config.maxLevels,
      config.maxIterations,
      config.tolerance,
      config.concurrency,
      progressTracker,
      executorService
    )

    louvain.setTerminationFlag(terminationFlag)

    val louvainResult: LouvainResult = louvain.compute

    (louvainResult.dendrogramManager.getAllDendrograms, louvainResult.modularities)
  }



}
