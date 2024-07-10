package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.cai.pandadb.algoconfig.PandaLouvainConfig
import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.lynx.types.structural.LynxNode
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.loading.construction.RelationshipsBuilder.Relationship
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

/**
 * @author cai584770
 * @date 2024/7/10 9:38
 * @Version
 */
class LouvainFunction extends TypeFunctions {

//  @LynxProcedure(name = "Louvain.compute")
//  def compute(nodes: Iterator[LynxNode], @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,@LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString): (Array[HugeLongArray],Array[Double]) = {
//    val nodeIds = nodes.map(_.id).toList
//    val (nodeRecords, relationshipsRecords) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath.value,nodeLabel.value,relationshipLabel.value)
//
//    val hugeGraph = GraphConversion.convert(nodeRecords,relationshipsRecords,RelationshipType.of(relationshipLabel.value))
//
//    PandaLouvainConfig.louvain(hugeGraph,nodeRecords.length, TOLERANCE_DEFAULT,10,includeIntermediateCommunities = true,1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)
//
//  }

}
