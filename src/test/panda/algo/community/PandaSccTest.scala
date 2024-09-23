package panda.algo.community

import org.cai.algoconfig.community.PandaSCCConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType

/**
 * @author cai584770
 * @date 2024/7/10 13:09
 * @Version
 */
class PandaSccTest {

  @Test
  def sccTest(): Unit = {
    val dbPath = "/home/cjw/lsbc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

    val array = PandaSCCConfig.scc(hg)

    println(array)

  }


}
