package panda.algo.Centrality

import org.cai.algoconfig.centrality.PandaBetweennessCentralityConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray

/**
 * @author cai584770
 * @date 2024/7/20 10:50
 * @Version
 */
class PandaBetweennessCentralityTest {

  private val dbPath = "/home/cjw/db/bc.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "FOLLOWS")
  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("FOLLOWS"))

  @Test
  def PBCTest():Unit={
    val result: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hg)
    val le = hg.idMap().nodeCount().toInt
    println(nodeResult(0).values.getClass)
    println(nodeResult(0).cols.getClass)

    for (l <- 0 until le){
      println(nodeResult(l).values + ":" +result.get(l.toLong))
    }

  }


}
