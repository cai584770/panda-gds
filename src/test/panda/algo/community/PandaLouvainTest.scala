package panda.algo.community

import org.cai.algoconfig.community.PandaLouvainConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
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
    val dbPath = "/home/cjw/db/louvain.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "LINK")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("LINK"))

    val (dendrogram,modularities) = PandaLouvainConfig.louvain(hg, TOLERANCE_DEFAULT, 10, includeIntermediateCommunities = true, 1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)

    val result = dendrogram(0).toArray

    for (i <-  0 until hg.idMap().nodeCount().toInt) {
      println(nodeResult(i).values+":"+result(i))
    }
  }






}
