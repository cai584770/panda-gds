package org.cai.CommunityDiscoveryAlgoFunc

import org.cai.algoconfig.PandaDFSConfig
import org.cai.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, DfsBaseConfig, ExitPredicate, TargetExitPredicate}

import scala.collection.JavaConverters.seqAsJavaListConverter

/**
 * @author cai584770
 * @date 2024/7/23 16:07
 * @Version
 */
class DFSFunction extends TypeFunctions {

  @LynxProcedure(name = "DFS.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               targets: List[java.lang.Long] = List.empty[java.lang.Long],
               aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION,
               maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
             ): Array[Long] = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val exitPredicate: ExitPredicate  = new TargetExitPredicate(targets.asJava)

    val result: Array[Long] = PandaDFSConfig.DFS(hugeGraph, exitPredicate,aggregatorFunction, maxDepth, progressTracker)

    result
  }
  
}
