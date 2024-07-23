package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.PandaPageRankConfig
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode

import java.util.function.LongToDoubleFunction

/**
 * @author cai584770
 * @date 2024/7/20 10:24
 * @Version
 */
class PageRankFunction {

  @LynxProcedure(name = "PageRank.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               maxIterations: Int = 40,
               concurrency: Int = 1,
               tolerance: Int = 0,
               mode: Mode = Mode.PAGE_RANK,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
             ): LongToDoubleFunction = {

    val (nodeRecords,relationshipsRecords) = BaseFunction.query(nodeLabel,relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result = PandaPageRankConfig.pageRank(hugeGraph,maxIterations,concurrency, tolerance, mode, progressTracker)

    result
  }

}
