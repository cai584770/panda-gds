package pandadbgds.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.PandaBFSConfig
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, DfsBaseConfig, ExitPredicate, TargetExitPredicate}

import scala.collection.JavaConverters.seqAsJavaListConverter

/**
 * @author cai584770
 * @date 2024/7/23 16:27
 * @Version
 */
class BFSFunction extends TypeFunctions {

  @LynxProcedure(name = "BFS.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               source: Long = -1L,
               targets: List[java.lang.Long] = List.empty[java.lang.Long],
               aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION,
               concurrency: Int = 1,
               maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
             ): Array[Long] = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val exitPredicate: ExitPredicate = new TargetExitPredicate(targets.asJava)

    val result: Array[Long] = PandaBFSConfig.BFS(hugeGraph, source,
      exitPredicate,
      aggregatorFunction,
      concurrency,
      progressTracker,
      maxDepth)

    result
  }

}
