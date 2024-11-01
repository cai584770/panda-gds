package org.cai.algofunc

import org.cai.algoconfig.community.{PandaLabelPropagationConfig, PandaLouvainConfig, PandaTriangleCountConfig, PandaWCCConfig}
import org.cai.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.{LynxList, LynxMap}
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
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/25 18:41
 * @Version
 */
class CommunityDetectionFunctions extends TypeFunctions {

  private val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

  @LynxProcedure(name = "Louvain.compute")
  def louvainCompute(
                      @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                      @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString
                    ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val (dendrogram, modularities) = PandaLouvainConfig.louvain(hugeGraph)
    val count: Int = hugeGraph.idMap().nodeCount().toInt
    val result = dendrogram(0).toArray

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeRecords(cursor).values.toList).toString -> LynxValue(result(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
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
    val count: Int = hugeGraph.idMap().nodeCount().toInt

    val lpResult: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hugeGraph, concurrency, maxIterations, nodeWeightProperty, executorService, progressTracker)

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeRecords(cursor).values.toList).toString -> LynxValue(lpResult(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
  }


  @LynxProcedure(name = "WCC.compute")
  def computeWCC(
                  @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                  @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val longs: Array[Long] = PandaWCCConfig.wcc(hugeGraph)

    val count: Int = hugeGraph.idMap().nodeCount().toInt

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeRecords(cursor).values.toList).toString -> LynxValue(longs(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
  }

//  @LynxProcedure(name = "TriangleCount.compute")
//  def computeTriangleCount(
//                            @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
//                            @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
//                          ): LynxValue = {
//
//    val javaMap: JMap[String, Object] = null
//    val executorService: ExecutorService = DefaultPool.INSTANCE
//    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
//
//    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
//    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
//    val tx: PandaTransaction = embeddedDB.beginTransaction()
//
//    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
//    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList
//
//    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))
//
//    val pandaLabelPropagationResult: Long = PandaTriangleCountConfig.triangleCount(hugeGraph, javaMap, progressTracker, executorService)
//
//    val lynxResult: LynxValue = LynxValue.apply(pandaLabelPropagationResult)
//
//    lynxResult
//  }

}

