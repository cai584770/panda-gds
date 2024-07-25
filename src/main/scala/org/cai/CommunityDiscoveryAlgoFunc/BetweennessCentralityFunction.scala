package org.cai.CommunityDiscoveryAlgoFunc

import org.cai.algoconfig.PandaBetweennessCentralityConfig
import org.cai.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.betweenness.{ForwardTraverser, FullSelectionStrategy, SelectionStrategy}
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker

import java.util.concurrent.ExecutorService

/**
 * @author cai584770
 * @date 2024/7/23 15:59
 * @Version
 */
class BetweennessCentralityFunction extends TypeFunctions {

  @LynxProcedure(name = "BetweennessCentrality.compute")
  def compute(
               @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
               @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
               selectionStrategy: SelectionStrategy = new FullSelectionStrategy,
               traverserFactory: ForwardTraverser.Factory = ForwardTraverser.Factory.unweighted,
               executorService: ExecutorService = DefaultPool.INSTANCE,
               concurrency: Int = 1,
               progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
             ): HugeAtomicDoubleArray = {

    val (nodeRecords, relationshipsRecords) = BaseFunction.query(nodeLabel, relationshipLabel)

    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val result: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hugeGraph, selectionStrategy, traverserFactory, executorService, concurrency, progressTracker)

    result
  }

}
