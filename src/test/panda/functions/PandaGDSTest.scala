package panda.functions

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.LynxResult
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.GraphDataBaseBuilder
import org.grapheco.pandadb.graph.{LazyPandaRelationship, PandaNode}
import org.junit.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.huge.HugeGraph


/**
 * @author cai584770
 * @date 2024/7/9 10:07
 * @Version
 */
class PandaGDSTest {

  @Test
  def getNodes(): Unit = {
    val path = "/home/cjw/lsbc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("match (n:Person) return n;")
    val records = result.records()
    while (records.hasNext) {
      val record = records.next()
      val node = record.values.head.value
      //      println(record.cols)
      node match {
        case n: PandaNode => println(s"n.id:${n.id},n.labels:${n.labels}")
        case _ => println("other")
      }
    }

    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def getRelationships(): Unit = {

    val path = "/home/cjw/lsbc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("MATCH (n:Person)-[r:KNOWS]->(m:Person) RETURN r")
    val records = result.records()
    while (records.hasNext) {
      val record = records.next()
      val relationship = record.values.head.value
      //      println(relationship.getClass)
      relationship match {
        case n: LazyPandaRelationship => println(s"id relationship:${n.startId}-${n.relationType}-${n.endId}")
        case _ => println("other")
      }
    }

    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def louvainTest(): Unit = {
    val path = "/home/cjw/lsbc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val query =
      """
        |call Louvain.compute('/home/cjw/lsbc.db','Person','KNOWS')
        |""".stripMargin
    val result: LynxResult = tx.executeQuery(query)


    tx.commit()
    tx.close()
    db.close()
  }



}
