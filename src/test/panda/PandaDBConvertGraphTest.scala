package panda

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType

/**
 * @author cai584770
 * @date 2024/7/9 16:03
 * @Version
 */
class PandaDBConvertGraphTest {

  @Test
  def convertGraphTest():Unit={
    val dbPath = "/home/cjw/lsbc.db"
    val (nodeResult,relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath,"Person","KNOWS")

    val hg = GraphConversion.convert(nodeResult,relationshipResult,RelationshipType.of("KNOWS"))
  }
}
