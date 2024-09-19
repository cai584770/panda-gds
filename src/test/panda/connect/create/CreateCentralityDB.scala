package panda.connect.create

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/9/19 10:23
 * @Version
 */
class CreateCentralityDB {

  /***
   * create Betweenness Centrality example DB
   * https://neo4j.com/docs/graph-data-science/2.6/algorithms/betweenness-centrality/#algorithms-betweenness-centrality-examples
   */
  @Test
  def createBCDB(): Unit = {
    val path = "/home/cjw/db/bc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    tx.executeQuery(
      """
        |CREATE
        |  (alice:User {name: 'Alice'}),
        |  (bob:User {name: 'Bob'}),
        |  (carol:User {name: 'Carol'}),
        |  (dan:User {name: 'Dan'}),
        |  (eve:User {name: 'Eve'}),
        |  (frank:User {name: 'Frank'}),
        |  (gale:User {name: 'Gale'}),
        |
        |  (alice)-[:FOLLOWS {weight: 1.0}]->(carol),
        |  (bob)-[:FOLLOWS {weight: 1.0}]->(carol),
        |  (carol)-[:FOLLOWS {weight: 1.0}]->(dan),
        |  (carol)-[:FOLLOWS {weight: 1.3}]->(eve),
        |  (dan)-[:FOLLOWS {weight: 1.0}]->(frank),
        |  (eve)-[:FOLLOWS {weight: 0.5}]->(frank),
        |  (frank)-[:FOLLOWS {weight: 1.0}]->(gale);
        |""".stripMargin
    )
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  /***
   * Create Page Rank Algo DB example
   *https://neo4j.com/docs/graph-data-science/2.6/algorithms/page-rank/#algorithms-page-rank-examples
   *
   */
  @Test
  def createPageRankDB(): Unit = {
    val path = "/home/cjw/db/pr.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      """
        |CREATE
        |  (home:Page {name:'Home'}),
        |  (about:Page {name:'About'}),
        |  (product:Page {name:'Product'}),
        |  (links:Page {name:'Links'}),
        |  (a:Page {name:'Site A'}),
        |  (b:Page {name:'Site B'}),
        |  (c:Page {name:'Site C'}),
        |  (d:Page {name:'Site D'}),
        |
        |  (home)-[:LINKS {weight: 0.2}]->(about),
        |  (home)-[:LINKS {weight: 0.2}]->(links),
        |  (home)-[:LINKS {weight: 0.6}]->(product),
        |  (about)-[:LINKS {weight: 1.0}]->(home),
        |  (product)-[:LINKS {weight: 1.0}]->(home),
        |  (a)-[:LINKS {weight: 1.0}]->(home),
        |  (b)-[:LINKS {weight: 1.0}]->(home),
        |  (c)-[:LINKS {weight: 1.0}]->(home),
        |  (d)-[:LINKS {weight: 1.0}]->(home),
        |  (links)-[:LINKS {weight: 0.8}]->(home),
        |  (links)-[:LINKS {weight: 0.05}]->(a),
        |  (links)-[:LINKS {weight: 0.05}]->(b),
        |  (links)-[:LINKS {weight: 0.05}]->(c),
        |  (links)-[:LINKS {weight: 0.05}]->(d);
        |
        |""".stripMargin)
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

}
