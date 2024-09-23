package panda.algo.pathfinding

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.BFS
import org.neo4j.gds.paths.traverse.ExitPredicate.Result

/**
 * @author cai584770
 * @date 2024/9/20 10:11
 * @Version
 */
class PandaBFSTest{
  private val dbPath = "/home/cjw/db/bfs.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("REL"))


  @Test
  def BFSTest(): Unit = {
    val source: Long = hg.toMappedNodeId(1L)
    val target: Long = hg.toMappedNodeId(4L)

    val result: Array[Long] = BFS.create(
      hg,
      source,
      (s: Long, t: Long, w: Double) => if (t == target) Result.BREAK
      else Result.FOLLOW,
      (s: Long, t: Long, w: Double) => 1.0,
      1,
      ProgressTracker.NULL_TRACKER,
      BFS.ALL_DEPTHS_ALLOWED
    ).compute().toArray

    val count: Int = result.length
    for (cursor <- 0 until count) {
      println(nodeResult(result(cursor).toInt).values+ ":" + result(cursor))
    }

    println(result.mkString("-"))

  }


}
