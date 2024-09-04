package org.cai.algofunc

import org.cai.algoconfig.community.{PandaLabelPropagationConfig, PandaLouvainConfig, PandaTriangleCountConfig, PandaWCCConfig}
import org.cai.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

import java.util.concurrent.ExecutorService
import java.util.{Map => JMap}

/**
 * @author cai584770
 * @date 2024/7/25 18:41
 * @Version
 */
class CommunityDetectionFunctions extends TypeFunctions {

  private val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

  @LynxProcedure(name = "Louvain.compute")
  def computeLouvain(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxList,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString
             ): LynxValue = {
    val tolerance: Double = TOLERANCE_DEFAULT
    val maxIterations: Int = 10
    val includeIntermediateCommunities: Boolean = true
    val concurrency: Int = 1
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
    val executorService: ExecutorService = DefaultPool.INSTANCE
    val terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val (dendrogram,modularities): (Array[HugeLongArray], Array[Double]) = PandaLouvainConfig.louvain(hugeGraph, tolerance, maxIterations, includeIntermediateCommunities, concurrency, progressTracker, executorService, terminationFlag)

    val modularitiesLynx: LynxValue = LynxValue.apply(modularities)

    for (den: HugeLongArray <- dendrogram) {
      val d: Array[Long] = den.toArray()
      val v: LynxValue = LynxValue.apply(d)
    }

    tx.commit()
    tx.close()

    modularitiesLynx
  }


  @LynxProcedure(name = "LabelPropagation.compute")
  def computeLP(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                            ): LynxValue = {

    val concurrency: Int = 1
    val maxIterations: Int = 10
    val nodeWeightProperty: String = null
    val executorService: ExecutorService = DefaultPool.INSTANCE
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER


    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val pandaLabelPropagationResult: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hugeGraph, concurrency, maxIterations, nodeWeightProperty, executorService, progressTracker)

    val lynxResult: LynxValue = LynxValue.apply(pandaLabelPropagationResult)

    lynxResult
  }


  @LynxProcedure(name = "WCC.compute")
  def computeWCC(
                 @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                 @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               ): LynxValue = {

    val javaMap: JMap[String, Object] = JMap.of(
      "threshold", 3.14.asInstanceOf[AnyRef],
      "relationshipWeightProperty", "threshold",
      "mutateProperty", "resultProperty"
    )
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val pandaWCCResult: Array[Long] = PandaWCCConfig.wcc(hugeGraph, javaMap ,progressTracker)

    val lynxResult: LynxValue = LynxValue.apply(pandaWCCResult)

    lynxResult
  }

  @LynxProcedure(name = "TriangleCount.compute")
  def computeTriangleCount(
                 @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                 @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               ): LynxValue = {

    val javaMap: JMap[String, Object] = null
    val executorService: ExecutorService = DefaultPool.INSTANCE
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val pandaLabelPropagationResult: Long = PandaTriangleCountConfig.triangleCount(hugeGraph, javaMap, progressTracker, executorService)

    val lynxResult: LynxValue = LynxValue.apply(pandaLabelPropagationResult)

    lynxResult
  }

}

