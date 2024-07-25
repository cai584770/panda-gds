package panda.algo

import org.cai.algoconfig.PandaBetweennessCentralityConfig
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

  private val dbPath = "/home/cjw/lp.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "FOLLOW")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("FOLLOW"))

  @Test
  def PBCTest():Unit={
    val result: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hg)
    val le = hg.idMap().nodeCount().toInt

    for (l <- 0 until le){
      println(result.get(l.toLong))

    }

  }


}
