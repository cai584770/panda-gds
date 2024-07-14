package panda.algo

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType

/**
 * @author cai584770
 * @date 2024/7/12 10:49
 * @Version
 */
class PandaLPTest {

  @Test
  def lpTest():Unit={
    val dbPath = "/home/cjw/lsbc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val hg = GraphConversion.convert(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

//    new LabelPropagationBaseConfig
//
//    val propagation = new LabelPropagation(hg, new LabelPropagationBaseConfig(new Concurrency() {
//      override def concurrency() = 1
//    }, 1, null, null), DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER, TerminationFlag.RUNNING_TRUE)
  }

}
