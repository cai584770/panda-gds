package panda.connect

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/7/15 12:04
 * @Version
 */
class CreateDBTest {

  @Test
  def delete(): Unit = {
    val path = "/home/cjw/lp.db"
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
  def createLPDB(): Unit = {
    val path = "/home/cjw/lp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val query = ""
    tx.executeQuery(
      """
        |CREATE
        |(alice:User {name: 'Alice', posts: 4, seed_label: 52}),
        |(bridget:User {name: 'Bridget', posts: 13, seed_label: 21}),
        |(charles:User {name: 'Charles', posts: 55, seed_label: 43}),
        |(doug:User {name: 'Doug', posts: 5, seed_label: 21}),
        |(mark:User {name: 'Mark', posts: 7, seed_label: 19}),
        |(michael:User {name: 'Michael', posts: 15, seed_label: 52}),
        |(alice)-[:FOLLOW {weight: 1}]->(bridget),
        |(alice)-[:FOLLOW {weight: 10}]->(charles),
        |(mark)-[:FOLLOW {weight: 1}]->(doug),
        |(bridget)-[:FOLLOW {weight: 1}]->(michael),
        |(doug)-[:FOLLOW {weight: 1}]->(mark),
        |(michael)-[:FOLLOW {weight: 1}]->(alice),
        |(alice)-[:FOLLOW {weight: 1}]->(michael),
        |(bridget)-[:FOLLOW {weight: 1}]->(alice),
        |(michael)-[:FOLLOW {weight: 1}]->(bridget),
        |(charles)-[:FOLLOW {weight: 1}]->(doug);
        |""".stripMargin)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }


}
