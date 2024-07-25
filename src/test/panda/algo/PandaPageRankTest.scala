package panda.algo

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
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
