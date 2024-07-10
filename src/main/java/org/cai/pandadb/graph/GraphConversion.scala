package org.cai.pandadb.graph

import org.grapheco.lynx.{LynxRecord, LynxResult}
import org.grapheco.pandadb.graph.{LazyPandaNode, LazyPandaRelationship, PandaNode}
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.loading.SingleTypeRelationships
import org.neo4j.gds.core.loading.construction.{GraphFactory, RelationshipsBuilder}
import org.neo4j.gds.{Orientation, RelationshipType}

import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/7/8 16:11
 * @Version
 */
object GraphConversion {


  def convert(nodesRecordsList: immutable.Seq[LynxRecord], relationshipsRecordList: immutable.Seq[LynxRecord], relationshipType: RelationshipType): HugeGraph = {

    val nodesCount = nodesRecordsList.length
    val lastNode: LynxRecord = nodesRecordsList.last
    val maxNodeId: Long = lastNode.values.head.value match {
      case pn: PandaNode => pn.longId
      case _ => println("Max Node Id Not Found!")
        0L
    }

    val nodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(maxNodeId).hasLabelInformation(false).build

    var i = 1
    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
        if (i == 1) {
          i += 1
        }
        record match {
          case pn: PandaNode =>
            val id = pn.longId
            nodesBuilder.addNode(id)
          case _ => println("Panda Node?")
        }
    }

    val nodes = nodesBuilder.build()

    val relationshipsBuilder: RelationshipsBuilder = GraphFactory
      .initRelationshipsBuilder.nodes(nodes.idMap).relationshipType(relationshipType).orientation(Orientation.NATURAL).build

    relationshipsRecordList.foreach {
      n =>
        val record = n.values.head.value
        if (i == 2) {
          i += 1
        }
        record match {
          case pr: LazyPandaRelationship =>
            val startNodeId = pr.startId
            val endNodeId = pr.endId
            relationshipsBuilder.add(startNodeId, endNodeId)
          case _ => println("Panda Relationship?")
        }
    }

    val relationships: SingleTypeRelationships = relationshipsBuilder.build()

    GraphFactory.create(nodes.idMap, relationships)

  }


}
