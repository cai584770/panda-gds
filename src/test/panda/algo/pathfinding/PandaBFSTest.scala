package panda.algo.pathfinding

import org.cai.algoconfig.path.PandaBFSConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxMap
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.BFS
import org.neo4j.gds.paths.traverse.ExitPredicate.Result

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

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

  @Test
  def BFS1Test(): Unit = {
    val source: Long = hg.toMappedNodeId(1L)
    val target: Long = hg.toMappedNodeId(4L)

    val targetList = List(4L)
    val result: Array[Long] = PandaBFSConfig.BFS(hg,source,targetList)
    val count: Int = result.length

    val mapListBuffer = ListBuffer[LynxValue]()
    for (cursor <- 0 until count) {
      mapListBuffer += LynxValue(nodeResult(cursor).values.toList)
    }

    val mapList: immutable.Seq[LynxValue] = mapListBuffer.toList
    LynxValue(mapList)
    println(LynxValue(mapList))

  }


}
