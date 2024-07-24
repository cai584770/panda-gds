package pandadbgds.graph

import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.{LynxRecord, LynxResult}
import org.grapheco.pandadb.graph.{LazyPandaNode, LazyPandaRelationship, PandaNode}
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.loading.{Nodes, SingleTypeRelationships}
import org.neo4j.gds.core.loading.construction.{GraphFactory, NodeLabelToken, NodeLabelTokens, NodesBuilder, RelationshipsBuilder}
import org.neo4j.gds.{Orientation, RelationshipType}
import org.neo4j.values.storable.{Value, Values}
import scala.collection.JavaConverters._

import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/7/8 16:11
 * @Version
 */
object GraphConversion {


  def convertWithId(nodesRecordsList: immutable.Seq[LynxRecord], relationshipsRecordList: immutable.Seq[LynxRecord], relationshipType: RelationshipType): HugeGraph = {
    val nodesCount = nodesRecordsList.length
    val lastNode: LynxRecord = nodesRecordsList.last
    val maxNodeId: Long = lastNode.values.head.value match {
      case pn: PandaNode => pn.longId
      case _ => println("Max Node Id Not Found!")
        0L
    }

    val nodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(maxNodeId).hasLabelInformation(false).build

    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
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

  def convertWithNodeLabel(nodesRecordsList: immutable.Seq[LynxRecord], relationshipsRecordList: immutable.Seq[LynxRecord], relationshipType: RelationshipType): HugeGraph = {
    val nodesCount: Int = nodesRecordsList.length
    val lastNode: LynxRecord = nodesRecordsList.last
    val maxNodeId: Long = lastNode.values.head.value match {
      case pn: PandaNode => pn.longId
      case _ => println("Max Node Id Not Found!")
        0L
    }

    val nodesBuilder: NodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(maxNodeId).concurrency(1).hasLabelInformation(false).build


    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pn: PandaNode =>
            val gdsId: Long = pn.longId

            val properties: Seq[(String, LynxValue)] = pn.props
            val gdsProperties: Map[String, Value] = properties.foldLeft(Map.empty[String, Value]) { (acc, property) =>
              val key: String = property._1
              val value = property._2.value
              acc + (key -> Values.of(value))
            }

            val gdsPropertiesJava: java.util.Map[String, org.neo4j.values.storable.Value] = gdsProperties.asJava

            val labels: Array[String] = pn.labels.map(_.value).toArray
            val gdsNodeLabelTokens: NodeLabelToken = NodeLabelTokens.ofNullable(labels)

            nodesBuilder.addNode(gdsId, gdsNodeLabelTokens)
          case _ => println("Panda Node?")
        }
    }

    val nodes: Nodes = nodesBuilder.build()

    val relationshipsBuilder: RelationshipsBuilder = GraphFactory
      .initRelationshipsBuilder.nodes(nodes.idMap).relationshipType(relationshipType).orientation(Orientation.NATURAL).build

    relationshipsRecordList.foreach {
      n =>
        val record = n.values.head.value
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
