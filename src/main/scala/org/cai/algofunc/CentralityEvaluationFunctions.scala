package org.cai.algofunc

import org.cai.algoconfig.centrality.{PandaBetweennessCentralityConfig, PandaPageRankConfig}
import org.cai.graph.GraphConversion
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.{LynxList, LynxMap}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.betweenness.{ForwardTraverser, FullSelectionStrategy, SelectionStrategy}
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode

import java.util.concurrent.ExecutorService
import java.util.function.LongToDoubleFunction
import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/25 18:41
 * @Version
 */
class CentralityEvaluationFunctions extends TypeFunctions {

  private val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]
  private val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

  @LynxProcedure(name = "BetweennessCentrality.compute")
  def computeBC(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
             ): LynxValue = {
//    call louvain.compute("","") ->(["",""],[])
    val selectionStrategy: SelectionStrategy = new FullSelectionStrategy
    val traverserFactory: ForwardTraverser.Factory = ForwardTraverser.Factory.unweighted
    val executorService: ExecutorService = DefaultPool.INSTANCE
    val concurrency: Int = 1

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords: immutable.Seq[LynxRecord] = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))
    val betweennessCentralityResult: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hugeGraph, selectionStrategy, traverserFactory, executorService, concurrency, progressTracker)
    val count: Int = hugeGraph.idMap().nodeCount().toInt

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeRecords(cursor).values.toList).toString -> LynxValue(betweennessCentralityResult.get(cursor.toLong)))
      mapListBuffer += map
    }


    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
  }

  @LynxProcedure(name = "PageRank.compute")
  def computePR(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                           ): LynxValue = {

    val maxIterations: Int = 40
    val concurrency: Int = 1
    val tolerance: Int = 0
    val mode: Mode = Mode.PAGE_RANK
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords: immutable.Seq[LynxRecord] = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords: immutable.Seq[LynxRecord] = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))
    val count: Int = hugeGraph.idMap().nodeCount().toInt

    val pageRankResult: LongToDoubleFunction = PandaPageRankConfig.pageRank(hugeGraph, maxIterations, concurrency, tolerance,0.85, mode, progressTracker)

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeRecords(cursor).values.toList).toString -> LynxValue(pageRankResult.applyAsDouble(cursor.toLong)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
  }


}

