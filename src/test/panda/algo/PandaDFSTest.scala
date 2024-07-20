package panda.algo

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.{Aggregator, BFS, DFS, DfsBaseConfig}
import org.neo4j.gds.paths.traverse.ExitPredicate.Result
import org.neo4j.gds.termination.TerminationFlag

/**
 * @author cai584770
 * @date 2024/7/20 15:56
 * @Version
 */
class PandaDFSTest {
  private val dbPath = "/home/cjw/pf.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("REL"))

  @Test
  def DFSTest():Unit={
    val result: Array[Long] = new DFS(hg, hg.toMappedNodeId(1L), (s: Long, t: Long, w: Double) => Result.FOLLOW, Aggregator.NO_AGGREGATION, DfsBaseConfig.NO_MAX_DEPTH, ProgressTracker.NULL_TRACKER).compute.toArray

    println(result.mkString("-"))

  }

  @Test
  def BFSTest(): Unit ={
    val source: Long = hg.toMappedNodeId(0L)
    val target: Long = hg.toMappedNodeId(1L)

    val nodes: Array[Long] = BFS.create(
      hg,
      source,
      (s: Long, t: Long, w: Double) => if (t == target) Result.BREAK
      else Result.FOLLOW,
      (s: Long, t: Long, w: Double) => 1.0,
      1,
      ProgressTracker.NULL_TRACKER,
      1
    ).compute().toArray

    println(nodes.mkString("-"))

  }


}
