package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.PandaLouvainConfig
import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.lynx.types.structural.LynxNode
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.loading.construction.RelationshipsBuilder.Relationship
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

import java.util.concurrent.ExecutorService
import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/7/10 9:38
 * @Version
 */
class LouvainFunction extends TypeFunctions {


  @LynxProcedure(name = "Louvain.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               tolerance: Double = TOLERANCE_DEFAULT,
               maxIterations: Int = 10,
               includeIntermediateCommunities: Boolean = true,
               concurrency: Int = 1,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
               executorService: ExecutorService = DefaultPool.INSTANCE,
               terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
             ): (Array[HugeLongArray], Array[Double]) = {

    val (nodeRecords,relationshipsRecords) = BaseFunction.query(nodeLabel,relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: (Array[HugeLongArray], Array[Double]) = PandaLouvainConfig.louvain(hugeGraph, tolerance, maxIterations, includeIntermediateCommunities, concurrency, progressTracker, executorService, terminationFlag)

    result

  }


}
