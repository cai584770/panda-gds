package panda.convert

import org.grapheco.lynx.{LynxRecord, LynxResult}
import org.grapheco.pandadb.GraphDataBaseBuilder
import org.grapheco.pandadb.facade.{GraphDatabaseService, PandaTransaction}
import org.grapheco.pandadb.graph.{LazyPandaRelationship, PandaNode}
import org.neo4j.gds.{Orientation, RelationshipType}
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.loading.SingleTypeRelationships
import org.neo4j.gds.core.loading.construction.{GraphFactory, RelationshipsBuilder}
import scala.collection.mutable
import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/10/30 16:48
 * @Version
 */
object GraphCovert {

  def convertWithId(dbPath: String, nodeLabel: String, relationshipLabel: String): (HugeGraph,mutable.Map[Int, Long],mutable.Map[Long, Int]) = {

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

    (GraphFactory.create(nodes, relationshipsBuilder.build()),nodeIdMap,nodeIdInverseMap)

  }


}
