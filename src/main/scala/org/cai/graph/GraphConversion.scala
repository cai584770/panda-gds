package org.cai.graph

import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.{LynxRecord, LynxResult}
import org.grapheco.pandadb.GraphDataBaseBuilder
import org.grapheco.pandadb.facade.{GraphDatabaseService, PandaTransaction}
import org.grapheco.pandadb.graph.{LazyPandaRelationship, PandaNode}
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.loading.construction._
import org.neo4j.gds.core.loading.{Nodes, SingleTypeRelationships}
import org.neo4j.gds.{Orientation, RelationshipType, api}
import org.neo4j.values.storable.{Value, Values}

import scala.collection.JavaConverters._
import scala.collection.{immutable, mutable}


/**
 * @author cai584770
 * @date 2024/7/8 16:11
 * @Version
 */
object GraphConversion {

  def getIdMap(nodesRecordsList: immutable.Seq[LynxRecord]): (org.neo4j.gds.api.IdMap, mutable.Map[Int, Long], mutable.Map[Long, Int]) = {
    val nodesCount: Int = nodesRecordsList.length
    val nodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(nodesCount - 1).hasLabelInformation(false).build

    val nodeIdMap = mutable.Map[Int, Long]()
    val nodeIdInverseMap = mutable.Map[Long, Int]()
    var id = 0
    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pn: PandaNode =>
            nodesBuilder.addNode(id)
            nodeIdMap += (id -> pn.longId)
            nodeIdInverseMap += (pn.longId -> id)
            id += 1
          case _ => println("Panda Node?")
        }
    }
    (nodesBuilder.build().idMap(), nodeIdMap, nodeIdInverseMap)
  }

  def createHugeGraph(relationshipsRecordList: immutable.Seq[LynxRecord], idMap: org.neo4j.gds.api.IdMap, nodeIdInverseMap: mutable.Map[Long, Int], relationship: String): HugeGraph = {
    val relationshipType = RelationshipType.of(relationship)

    val relationshipsBuilder: RelationshipsBuilder = GraphFactory
      .initRelationshipsBuilder.nodes(idMap).relationshipType(relationshipType).orientation(Orientation.NATURAL).build
    relationshipsRecordList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pr: LazyPandaRelationship =>
            val value1 = nodeIdInverseMap.getOrElse(pr.startId, -1)
            val value2 = nodeIdInverseMap.getOrElse(pr.endId, -1)
            relationshipsBuilder.add(value1.toLong, value2.toLong)
          case _ => println("Panda Relationship?")
        }
    }

    GraphFactory.create(idMap, relationshipsBuilder.build())
  }

  def convertWithId(dbPath: String, nodeLabel: String, relationshipLabel: String): (HugeGraph, mutable.Map[Int, Long], mutable.Map[Long, Int]) = {
    val relationshipType = RelationshipType.of("KNOWS")
    //  --------------------- get db data --------------------
    val nodesQuery = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"

    val db: GraphDatabaseService = GraphDataBaseBuilder.newEmbeddedDatabase(dbPath)
    val tx: PandaTransaction = db.beginTransaction()

    val nodeResult: LynxResult = tx.executeQuery(nodesQuery)
    val relationshipResult: LynxResult = tx.executeQuery(relationshipsQuery)

    val nodesRecordsList: immutable.Seq[LynxRecord] = nodeResult.records().toList
    val relationshipsRecordList: immutable.Seq[LynxRecord] = relationshipResult.records().toList

    tx.commit()
    tx.close()
    db.close()

    //  --------------- build nodes---------------------
    val nodesCount: Int = nodesRecordsList.length
    val nodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(nodesCount - 1).hasLabelInformation(false).build

    val nodeIdMap = mutable.Map[Int, Long]()
    val nodeIdInverseMap = mutable.Map[Long, Int]()
    var id = 0
    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pn: PandaNode =>
            nodesBuilder.addNode(id)
            nodeIdMap += (id -> pn.longId)
            nodeIdInverseMap += (pn.longId -> id)
            id += 1
          case _ => println("Panda Node?")
        }
    }

    val nodes = nodesBuilder.build().idMap()

    //  ---------------- build relationships----------------------
    val relationshipsBuilder: RelationshipsBuilder = GraphFactory
      .initRelationshipsBuilder.nodes(nodes).relationshipType(relationshipType).orientation(Orientation.NATURAL).build
    relationshipsRecordList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pr: LazyPandaRelationship =>
            val value1 = nodeIdInverseMap.getOrElse(pr.startId, -1)
            val value2 = nodeIdInverseMap.getOrElse(pr.endId, -1)

            relationshipsBuilder.add(value1.toLong, value2.toLong)

          case _ => println("Panda Relationship?")
        }
    }

    (GraphFactory.create(nodes, relationshipsBuilder.build()), nodeIdMap, nodeIdInverseMap)

  }


  @Deprecated
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
            println(id)
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
            println(s"startNodeId:$startNodeId --- endNodeId:$endNodeId")
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

  def idMap(nodesRecordsList: immutable.Seq[LynxRecord],
            relationshipsRecordList: immutable.Seq[LynxRecord],
            relationshipType: RelationshipType): Unit = {
    val nodesCount: Int = nodesRecordsList.length

    //    find last id => get max node id
    //    val lastNode: LynxRecord = nodesRecordsList.last
    //    val maxNodeId: Long = lastNode.values.head.value match {
    //      case pn: PandaNode => pn.longId
    //      case _ => println("Max Node Id Not Found!")
    //        0L
    //    }

    //    init id map
    val originalIds = new Array[Long](nodesCount)
    val mappedIds = new Array[Long](nodesCount)
    var idsCursor: Int = 0

    val nodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(nodesCount).hasLabelInformation(false).build

    nodesRecordsList.foreach {
      n =>
        val record = n.values.head.value
        record match {
          case pn: PandaNode =>
            originalIds(idsCursor) = pn.longId
            mappedIds(idsCursor) = idsCursor
            nodesBuilder.addNode(idsCursor)
            idsCursor += 1
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

    val idMap = new IdMap(originalIds, mappedIds)

    (idMap, GraphFactory.create(nodes.idMap, relationships))
  }


}
