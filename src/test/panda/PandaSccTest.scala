package panda

import org.cai.pandadb.algoconfig.{PandaLouvainConfig, PandaSCCConfig}
import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

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

    val hg = GraphConversion.convert(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

    val array = PandaSCCConfig.scc(hg)

    println(array)

  }


}
