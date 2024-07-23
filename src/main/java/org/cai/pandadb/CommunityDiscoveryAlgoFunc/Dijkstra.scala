package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.{PandaDFSConfig, PandaDijkstraSingleSourceShortestConfig, PandaDijkstraSourceTargetShortestConfig}
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.traverse.{Aggregator, DfsBaseConfig, ExitPredicate, TargetExitPredicate}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional

/**
 * @author cai584770
 * @date 2024/7/23 17:06
 * @Version
 */
class Dijkstra {

  @LynxProcedure(name = "Dijkstra.ss")
  def ss(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          source: Long,
          concurrency: Int = 1,
          trackRelationships: Boolean = false,
          heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction],
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
          terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
        ): util.Set[PathResult] = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hugeGraph, source, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    result
  }

  @LynxProcedure(name = "Dijkstra.st")
  def st(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          source: Long,
          target: Long,
          concurrency: Int = 1,
          trackRelationships: Boolean = false,
          heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction],
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
          terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE
        ): util.Set[PathResult] = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: util.Set[PathResult] = PandaDijkstraSourceTargetShortestConfig.dijkstraSourceTargetShortest(hugeGraph, source, target, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    result
  }

}
