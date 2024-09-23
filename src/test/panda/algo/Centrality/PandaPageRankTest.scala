package panda.algo.Centrality

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode
import org.neo4j.gds.pagerank.{PageRankAlgorithmFactory, PageRankStreamConfig, PageRankStreamConfigImpl}

import java.util.function.LongToDoubleFunction

/**
 * @author cai584770
 * @date 2024/7/12 12:08
 * @Version
 */
class PandaPageRankTest {
  private val SCORE_PRECISION = 1E-5
  private val dbPath = "/home/cjw/db/pr.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Page", "LINKS")

  private val hg: HugeGraph = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("LINKS"))


  @Test
  def pageRankTest(): Unit = {
    val build: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(20).concurrency(1).tolerance(0).dampingFactor(0.85).build

    val pageRankResult: LongToDoubleFunction = new PageRankAlgorithmFactory(Mode.PAGE_RANK).build(hg, build, ProgressTracker.NULL_TRACKER).compute().centralityScoreProvider()

    println(pageRankResult)

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until count){
      println(nodeResult(cursor).values+":"+pageRankResult.applyAsDouble(cursor.toLong))
    }

  }



}
