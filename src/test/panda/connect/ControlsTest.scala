package panda.connect

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/7/8 15:16
 * @Version
 */
class ControlsTest {

  @Test
  def deleteAll(): Unit = {
    val path = "/home/cjw/db/tc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery("MATCH ()-[r]->() DELETE r")
    tx.executeQuery("MATCH (n) DELETE n")
    val result = tx.executeQuery("match (n) return n limit 10")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def deleterelationships(): Unit = {
    val path = "/home/cjw/lp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery("MATCH ()-[r]->() DELETE r")
    val result = tx.executeQuery("match (n) return n limit 10")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def deletenodes(): Unit = {
    val path = "/home/cjw/lp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery("MATCH (n) DELETE n")
    val result = tx.executeQuery("match (n) return n limit 10")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }


  @Test
  def matchSomeNodes(): Unit = {
    val path = "/home/cjw/db/lsbc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("match (n:Person) return n limit 100;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def getDBInformation(): Unit = {
    val path = "/home/cjw/ldbc100t.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    println("NodeCount:")
    tx.executeQuery("MATCH (n) RETURN count(n) AS NumberOfNodes;")
    println("RelationshipCount:")
    tx.executeQuery("MATCH ()-[r]->() RETURN count(r) AS NumberOfRelationships")
    println("LabelTypeCount:")
    tx.executeQuery("MATCH (n) RETURN labels(n) AS NodeLabels, count(n) AS NumberOfNodes ORDER BY NumberOfNodes DESC")
    println("RelationshipTypeCount:")
    tx.executeQuery("MATCH ()-[r]->() RETURN type(r) AS RelationshipType, count(r) AS NumberOfRelationships ORDER BY NumberOfRelationships DESC")

    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def getRelationships(): Unit = {
    val path = "/home/cjw/ldbc100t.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("MATCH ()-[r]-() RETURN r LIMIT 10;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def getNodes(): Unit = {
    val path = "/home/cjw/tc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    println("NodeCount:")
    val result = tx.executeQuery("MATCH (n) RETURN n limit 10 ;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def getAllNodesAndRelationships(): Unit = {
    val path = "/home/cjw/db/tc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("MATCH (n)-[r]->(m) RETURN n, r, m;")
    result.show(100)
    tx.commit()
    tx.close()
    db.close()
  }

}
