package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.{PandaLabelPropagationConfig, PandaPageRankConfig}
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode

import java.util.concurrent.ExecutorService
import java.util.function.LongToDoubleFunction

/**
 * @author cai584770
 * @date 2024/7/20 10:32
 * @Version
 */
class LabelPropagationFunction {

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

    val nodesQuery = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx = BaseFunction.embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hugeGraph,concurrency, maxIterations, nodeWeightProperty, executorService, progressTracker)

    tx.commit()
    tx.close()
    result
  }
}
