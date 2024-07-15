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
  def createPageRankDB(): Unit = {
    val path = "/home/cjw/ppr.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    tx.executeQuery(
      """
        |CREATE
        |  (a:Node { expectedRank: 0.3040965, expectedPersonalizedRank1: 0.17053529152163158 , expectedPersonalizedRank2: 0.017454997930076894 }),
        |  (b:Node { expectedRank: 3.5604297, expectedPersonalizedRank1: 0.3216114449911402  , expectedPersonalizedRank2: 0.813246950528992    }),
        |  (c:Node { expectedRank: 3.1757906, expectedPersonalizedRank1: 0.27329311398643763 , expectedPersonalizedRank2: 0.690991752640184    }),
        |  (d:Node { expectedRank: 0.3625935, expectedPersonalizedRank1: 0.048318333106500536, expectedPersonalizedRank2: 0.041070583050331164 }),
        |  (e:Node { expectedRank: 0.7503465, expectedPersonalizedRank1: 0.17053529152163158 , expectedPersonalizedRank2: 0.1449550029964717   }),
        |  (f:Node { expectedRank: 0.3625935, expectedPersonalizedRank1: 0.048318333106500536, expectedPersonalizedRank2: 0.041070583050331164 }),
        |  (g:Node { expectedRank: 0.15     , expectedPersonalizedRank1: 0.0                 , expectedPersonalizedRank2: 0.0                  }),
        |  (h:Node { expectedRank: 0.15     , expectedPersonalizedRank1: 0.0                 , expectedPersonalizedRank2: 0.0                  }),
        |  (i:Node { expectedRank: 0.15     , expectedPersonalizedRank1: 0.0                 , expectedPersonalizedRank2: 0.0                  }),
        |  (j:Node { expectedRank: 0.15     , expectedPersonalizedRank1: 0.0                 , expectedPersonalizedRank2: 0.0                  }),
        |  (k:Node { expectedRank: 0.15     , expectedPersonalizedRank1: 0.0                 , expectedPersonalizedRank2: 0.15000000000000002  }),
        |  (b)-[:TYPE]->(c),
        |  (c)-[:TYPE]->(b),
        |  (d)-[:TYPE]->(a),
        |  (d)-[:TYPE]->(b),
        |  (e)-[:TYPE]->(b),
        |  (e)-[:TYPE]->(d),
        |  (e)-[:TYPE]->(f),
        |  (f)-[:TYPE]->(b),
        |  (f)-[:TYPE]->(e),
        |  (g)-[:TYPE]->(b),
        |  (g)-[:TYPE]->(e),
        |  (h)-[:TYPE]->(b),
        |  (h)-[:TYPE]->(e),
        |  (i)-[:TYPE]->(b),
        |  (i)-[:TYPE]->(e),
        |  (j)-[:TYPE]->(e),
        |  (k)-[:TYPE]->(e)
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
