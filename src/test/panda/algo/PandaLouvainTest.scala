package panda.algo

import org.cai.algoconfig.PandaLouvainConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag;

/**
 * @author cai584770
 * @date 2024/7/10 10:48
 * @Version
 */
class PandaLouvainTest {

  @Test
  def louvainTest(): Unit = {
    val dbPath = "/home/cjw/lsbc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

    val (dendrogram,modularities) = PandaLouvainConfig.louvain(hg, TOLERANCE_DEFAULT, 10, includeIntermediateCommunities = true, 1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)

    println(nodeResult.length)

    for (i <- dendrogram.indices) {
      System.out.println("dendrogram[" + i + "]:" + dendrogram(i))
      System.out.println("modularities[" + i + "]:" + modularities(i))
    }
  }






}
