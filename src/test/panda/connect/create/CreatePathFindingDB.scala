package panda.connect.create

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/9/19 10:22
 * @Version
 */
class CreatePathFindingDB {

  @Test
  def createDijkstraSourceTargetShortestPathDB(): Unit = {
    val path = "/home/cjw/db/dstsp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      """
        |CREATE (a:Location {name: 'A'}),
        |       (b:Location {name: 'B'}),
        |       (c:Location {name: 'C'}),
        |       (d:Location {name: 'D'}),
        |       (e:Location {name: 'E'}),
        |       (f:Location {name: 'F'}),
        |       (a)-[:ROAD {cost: 50}]->(b),
        |       (a)-[:ROAD {cost: 50}]->(c),
        |       (a)-[:ROAD {cost: 100}]->(d),
        |       (b)-[:ROAD {cost: 40}]->(d),
        |       (c)-[:ROAD {cost: 40}]->(d),
        |       (c)-[:ROAD {cost: 80}]->(e),
        |       (d)-[:ROAD {cost: 30}]->(e),
        |       (d)-[:ROAD {cost: 80}]->(f),
        |       (e)-[:ROAD {cost: 40}]->(f);
        |""".stripMargin
    )
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def createDSSSPDB(): Unit = {
    val path = "/home/cjw/db/dsssp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val query =
      """
        |CREATE (a:Location {name: 'A'}),
        |       (b:Location {name: 'B'}),
        |       (c:Location {name: 'C'}),
        |       (d:Location {name: 'D'}),
        |       (e:Location {name: 'E'}),
        |       (f:Location {name: 'F'}),
        |       (a)-[:ROAD {cost: 50}]->(b),
        |       (a)-[:ROAD {cost: 50}]->(c),
        |       (a)-[:ROAD {cost: 100}]->(d),
        |       (b)-[:ROAD {cost: 40}]->(d),
        |       (c)-[:ROAD {cost: 40}]->(d),
        |       (c)-[:ROAD {cost: 80}]->(e),
        |       (d)-[:ROAD {cost: 30}]->(e),
        |       (d)-[:ROAD {cost: 80}]->(f),
        |       (e)-[:ROAD {cost: 40}]->(f);
        |""".stripMargin
    tx.executeQuery(query)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def createBFSDB(): Unit = {
    val path = "/home/cjw/db/bfs.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    val query =
      """
        |CREATE
        |(nA:Node {name: 'A'}),
        |(nB:Node {name: 'B'}),
        |(nC:Node {name: 'C'}),
        |(nD:Node {name: 'D'}),
        |(nE:Node {name: 'E'}),
        |
        |(nA)-[:REL]->(nB),
        |(nA)-[:REL]->(nC),
        |(nB)-[:REL]->(nE),
        |(nC)-[:REL]->(nD);
        |""".stripMargin
    tx.executeQuery(query)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def createDFSDB(): Unit = {
    val path = "/home/cjw/db/dfs.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    val query =
      """
        |CREATE
        |(nA:Node {name: 'A'}),
        |(nB:Node {name: 'B'}),
        |(nC:Node {name: 'C'}),
        |(nD:Node {name: 'D'}),
        |(nE:Node {name: 'E'}),
        |
        |(nA)-[:REL]->(nB),
        |(nA)-[:REL]->(nC),
        |(nB)-[:REL]->(nE),
        |(nC)-[:REL]->(nD);
        |""".stripMargin
    tx.executeQuery(query)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

}
