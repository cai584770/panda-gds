package panda.algo.Centrality

import org.cai.algoconfig.centrality.PandaPageRankConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxMap
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode
import org.neo4j.gds.pagerank.{PageRankAlgorithmFactory, PageRankStreamConfig, PageRankStreamConfigImpl}

import java.util.function.LongToDoubleFunction
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/12 12:08
 * @Version
 */
class PandaPageRankTest {
  private val dbPath = "/home/cjw/db/pr.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Page", "LINKS")

  private val hg: HugeGraph = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("LINKS"))


  @Test
  def pageRankTest(): Unit = {
    val pageRankResult = PandaPageRankConfig.pageRank(hg)

    println(pageRankResult)

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until count){
      println(nodeResult(cursor).values+":"+pageRankResult.applyAsDouble(cursor.toLong))
    }

  }

  @Test
  def pageRankLynxTest(): Unit = {
    val pageRankResult = PandaPageRankConfig.pageRank(hg)

    val count: Int = hg.idMap().nodeCount().toInt

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeResult(cursor).values.toList).toString -> LynxValue(pageRankResult.applyAsDouble(cursor.toLong)))
      mapListBuffer += map
    }


    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))

    println(LynxValue(mapList.map(LynxMap)))

  }


}
