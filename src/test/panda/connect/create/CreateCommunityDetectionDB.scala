package panda.connect.create

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/9/19 10:23
 * @Version
 */
class CreateCommunityDetectionDB {
  /***
   * create louvain db example
   * https://neo4j.com/docs/graph-data-science/2.6/algorithms/louvain/#algorithms-louvain-examples
   */
  @Test
  def createLouvainDB(): Unit = {
    val path = "/home/cjw/louvain.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      """
        |CREATE
        |  (nAlice:User {name: 'Alice', seed: 42}),
        |  (nBridget:User {name: 'Bridget', seed: 42}),
        |  (nCharles:User {name: 'Charles', seed: 42}),
        |  (nDoug:User {name: 'Doug'}),
        |  (nMark:User {name: 'Mark'}),
        |  (nMichael:User {name: 'Michael'}),
        |
        |  (nAlice)-[:LINK {weight: 1}]->(nBridget),
        |  (nAlice)-[:LINK {weight: 1}]->(nCharles),
        |  (nCharles)-[:LINK {weight: 1}]->(nBridget),
        |
        |  (nAlice)-[:LINK {weight: 5}]->(nDoug),
        |
        |  (nMark)-[:LINK {weight: 1}]->(nDoug),
        |  (nMark)-[:LINK {weight: 1}]->(nMichael),
        |  (nMichael)-[:LINK {weight: 1}]->(nMark);
        |""".stripMargin)
    val result = tx.executeQuery("match (n) return n;")
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

  @Test
  def createWCCDB(): Unit = {
    val path = "/home/cjw/wcc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    tx.executeQuery(
      """
        |CREATE
        |  (a:Node),
        |  (b:Node),
        |  (c:Node),
        |  (d:Node),
        |  (e:Node),
        |  (f:Node),
        |  (g:Node),
        |  (h:Node),
        |  (i:Node),
        |  (j:Node),
        |  (a)-[:TYPE]->(b),
        |  (b)-[:TYPE]->(c),
        |  (c)-[:TYPE]->(d),
        |  (d)-[:TYPE]->(a),
        |  (e)-[:TYPE]->(f),
        |  (f)-[:TYPE]->(g),
        |  (g)-[:TYPE]->(e),
        |  (i)-[:TYPE]->(h),
        |  (h)-[:TYPE]->(i);
        |""".stripMargin
    )
    val result = tx.executeQuery("match (n) return n;")
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

  @Test
  def createTringleCountingDB(): Unit = {
    val path = "/home/cjw/tc1.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()

    tx.executeQuery(
      """
        |CREATE
        |  (a0:node),
        |  (a1:node),
        |  (a2:node),
        |  (a3:node),
        |  (a4:node),
        |  (aCenter:node),
        |  (a0)-[:R]->(a1),
        |  (a1)-[:R]->(a2),
        |  (a2)-[:R]->(a3),
        |  (a3)-[:R]->(a4),
        |  (a4)-[:R]->(a0),
        |  (aCenter)-[:R]->(a3),
        |  (aCenter)-[:R]->(a4),
        |  (aCenter)-[:R]->(a0),
        |  (aCenter)-[:R]->(a1),
        |  (aCenter)-[:R]->(a2);
        |""".stripMargin
    )
    val result = tx.executeQuery("MATCH (n)-[r]->(m) RETURN n, r, m;")
    result.show(100)
    tx.commit()
    tx.close()
    db.close()
  }


}
