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
  def createDijkstraDB(): Unit = {
    val path = "/home/cjw/dijkstra.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      """
        |CREATE
        |(a:Node{name: 'a'}), (b:Node{name: 'b'}), (c:Node{name: 'c'}), (d:Node{name: 'd'}), (e:Node{name: 'e'}), (f:Node{name: 'f'}),
        |(a)-[:TYPE {cost: 4}]->(b),
        |(a)-[:TYPE {cost: 2}]->(c),
        |(b)-[:TYPE {cost: 5}]->(c),
        |(b)-[:TYPE {cost: 10}]->(d),
        |(c)-[:TYPE {cost: 3}]->(e),
        |(d)-[:TYPE {cost: 11}]->(f),
        |(e)-[:TYPE {cost: 4}]->(d)
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
    val path = "/home/cjw/dsssp.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      "CREATE (a:A), (b:B), (c:C), (d:D), (e:E), (f:F), (a)-[:TYPE {cost: 4}]->(b), (a)-[:TYPE {cost: 2}]->(c), (b)-[:TYPE {cost: 5}]->(c), (b)-[:TYPE {cost: 10}]->(d), (c)-[:TYPE {cost: 3}]->(e), (d)-[:TYPE {cost: 11}]->(f), (e)-[:TYPE {cost: 4}]->(d)"
    )
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def createDFSBFSDB(): Unit = {
    val path = "/home/cjw/bfs.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    var query = "CREATE " + "  (a:Node)" + ", (b:Node)" + ", (c:Node)" + ", (d:Node)" + ", (e:Node)" + ", (f:Node)" + ", (g:Node)" + ", (a)-[:REL {cost:2.0}]->(b)" + ", (a)-[:REL {cost:1.0}]->(c)" + ", (b)-[:REL {cost:1.0}]->(d)" + ", (c)-[:REL {cost:2.0}]->(d)" + ", (d)-[:REL {cost:1.0}]->(e)" + ", (d)-[:REL {cost:2.0}]->(f)" + ", (e)-[:REL {cost:2.0}]->(g)" + ", (f)-[:REL {cost:1.0}]->(g)"

    query = "CREATE (a:Node)" + ", (b:Node)" + ", (c:Node)" + ", (d:Node)" + ", (e:Node)" + ", (f:Node)" + ", (g:Node)" + ", (a)-[:REL {cost:2.0}]->(b)" + ", (a)-[:REL {cost:1.0}]->(c)" + ", (b)-[:REL {cost:1.0}]->(d)" + ", (c)-[:REL {cost:2.0}]->(d)" + ", (d)-[:REL {cost:1.0}]->(e)" + ", (d)-[:REL {cost:2.0}]->(f)" + ", (e)-[:REL {cost:2.0}]->(g)" + ", (f)-[:REL {cost:1.0}]->(g)"
    tx.executeQuery(query)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

}
