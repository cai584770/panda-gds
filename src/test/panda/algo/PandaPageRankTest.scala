package panda.algo

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.pandadb.GraphDataBaseBuilder
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode
import org.neo4j.gds.pagerank.{PageRankAlgorithmFactory, PageRankStreamConfig, PageRankStreamConfigImpl}

/**
 * @author cai584770
 * @date 2024/7/12 12:08
 * @Version
 */
class PandaPageRankTest {
  private val SCORE_PRECISION = 1E-5
  private val dbPath = "/home/cjw/ppr.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

  private val hg: HugeGraph = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("TYPE"))

  @Test
  def createDB(): Unit = {
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
  def pageRankTest(): Unit = {
    val build: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(40).concurrency(1).tolerance(0).build

    val pageRankResult = new PageRankAlgorithmFactory(Mode.PAGE_RANK).build(hg, build, ProgressTracker.NULL_TRACKER).compute().centralityScoreProvider()

    println(pageRankResult)

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until count){
      println(pageRankResult.applyAsDouble(cursor.toLong))
    }

  }

  @Test
  def withTolerance():Unit ={
//    "0.5, 2"
//    "0.1, 13"
    val config: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(40).concurrency(1).tolerance(0.5).build
//    val provider: LongToDoubleFunction = runOnPregel(hg, config).centralityScoreProvider



  }


}
