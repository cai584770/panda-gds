package org.cai.AlgoFunc

import org.cai.algoconfig.{PandaBFSConfig, PandaDFSConfig, PandaDijkstraSingleSourceShortestConfig, PandaDijkstraSourceTargetShortestConfig}
import org.cai.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.{LynxInteger, LynxString}
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.traverse.{Aggregator, DfsBaseConfig, ExitPredicate, TargetExitPredicate}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional
import scala.collection.JavaConverters.seqAsJavaListConverter


/**
 * @author cai584770
 * @date 2024/7/25 18:41
 * @Version
 */
class PathSearchFunctions extends TypeFunctions {

  private val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

  @LynxProcedure(name = "BFS.compute")
  def computeBFS(
                  @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                  @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                  source: LynxInteger,
                  targets: LynxList
                ): LynxValue = {
    val aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION
    val concurrency: Int = 1
    val maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val target: List[LynxValue]  = targets.value

    val javaLongList: List[java.lang.Long] = target.collect {
      case lynxValue if lynxValue.value.isInstanceOf[Long] =>
        java.lang.Long.valueOf(lynxValue.value.asInstanceOf[Long])
    }

    val exitPredicate: ExitPredicate = new TargetExitPredicate(javaLongList.asJava)

    val BFSResult: Array[Long] = PandaBFSConfig.BFS(hugeGraph, source.value,
      exitPredicate,
      aggregatorFunction,
      concurrency,
      progressTracker,
      maxDepth)

    val lynxResult: LynxValue = LynxValue.apply(BFSResult)

    lynxResult

  }

  @LynxProcedure(name = "DFS.compute")
  def computeDFS(
                  @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                  @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                  source: LynxInteger,
                  targets: LynxList
                ): LynxValue = {

    val aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION
    val maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))


    val target: List[LynxValue] = targets.value

    val javaLongList: List[java.lang.Long] = target.collect {
      case lynxValue if lynxValue.value.isInstanceOf[Long] =>
        java.lang.Long.valueOf(lynxValue.value.asInstanceOf[Long])
    }

    val exitPredicate: ExitPredicate = new TargetExitPredicate(javaLongList.asJava)

    val DFSResult: Array[Long] = PandaDFSConfig.DFS(hugeGraph, source.value, exitPredicate, aggregatorFunction, maxDepth, progressTracker)

    val lynxResult: LynxValue = LynxValue.apply(DFSResult)

    lynxResult
  }

  @LynxProcedure(name = "Dijkstra.ss")
  def ss(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          source: LynxInteger
        ): LynxValue = {

    val concurrency: Int = 1
    val trackRelationships: Boolean = false
    val heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction]
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
    val terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val dijkstraSingleSourceShortestResult: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hugeGraph, source.value, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    var idsResult: Array[Array[Long]] = Array.empty[Array[Long]]

    val iterator = dijkstraSingleSourceShortestResult.iterator
    while (iterator.hasNext) {
      val dijkstraResult = iterator.next()
      idsResult = idsResult :+ dijkstraResult.nodeIds()
    }

    val result: LynxValue = LynxValue.apply(idsResult)

    result
  }

  @LynxProcedure(name = "Dijkstra.st")
  def st(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          source: LynxInteger,
          target: LynxInteger
        ): LynxValue = {
    val concurrency: Int = 1
    val trackRelationships: Boolean = false
    val heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction]
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
    val terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val dijkstraSourceTargetShortestResult: util.Set[PathResult] = PandaDijkstraSourceTargetShortestConfig.dijkstraSourceTargetShortest(hugeGraph, source.value, target.value, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    var idsResult: Array[Array[Long]] = Array.empty[Array[Long]]

    val iterator = dijkstraSourceTargetShortestResult.iterator
    while (iterator.hasNext) {
      val dijkstraResult = iterator.next()
      idsResult = idsResult :+ dijkstraResult.nodeIds()
    }

    val result: LynxValue = LynxValue.apply(idsResult)

    result
  }

}

