package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.PandaLouvainConfig
import org.cai.pandadb.graph.GraphConversion
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.lynx.types.structural.LynxNode
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.GraphFacade
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

/**
 * @author cai584770
 * @date 2024/7/10 9:38
 * @Version
 */
class LouvainFunctionExample extends TypeFunctions {

  @LynxProcedure(name = "Louvain.compute")
  def compute(nodes: Iterator[LynxNode], @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,@LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString): (Array[HugeLongArray],Array[Double]) = {

    val nodesQuery = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx = BaseFunction.embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList

    val hugeGraph = GraphConversion.convertWithId(nodeRecords,relationshipsRecords,RelationshipType.of(relationshipLabel.value))

    val result = PandaLouvainConfig.louvain(hugeGraph, TOLERANCE_DEFAULT,10,includeIntermediateCommunities = true,1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)

    tx.commit()
    tx.close()
    result
  }

}
