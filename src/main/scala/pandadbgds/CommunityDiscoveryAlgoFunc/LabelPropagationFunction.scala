package pandadbgds.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.{PandaBetweennessCentralityConfig, PandaLabelPropagationConfig}
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker

import java.util.concurrent.ExecutorService

/**
 * @author cai584770
 * @date 2024/7/20 10:32
 * @Version
 */
class LabelPropagationFunction extends TypeFunctions  {

  @LynxProcedure(name = "LabelPropagation.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               concurrency: Int = 1,
               maxIterations: Int = 10,
               nodeWeightProperty: String = null,
               executorService: ExecutorService = DefaultPool.INSTANCE,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
             ): Array[Long] = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hugeGraph, concurrency, maxIterations, nodeWeightProperty, executorService, progressTracker)

    result
  }
}
