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
  def deleterelationships(): Unit = {
    val path = "/home/cjw/lsbc.db"
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
    val path = "/home/cjw/lsbc.db"
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
  def matchAll(): Unit = {
    val path = "/data/cjw/ldbc100.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery("match (n:Person) return n limit 100;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }


}
