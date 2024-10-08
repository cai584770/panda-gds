package panda.algo.pathfinding

import org.cai.algoconfig.path.PandaDFSConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.api.IdMap
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.paths.traverse.ExitPredicate.Result
import org.neo4j.gds.paths.traverse.{Aggregator, BFS, DFS, DfsBaseConfig}

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/20 15:56
 * @Version
 */
class PandaDFSTest {
  private val dbPath = "/home/cjw/db/dfs.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("REL"))

  @Test
  def DFSTest(): Unit = {
    val result: Array[Long] = new DFS(hg, hg.toMappedNodeId(1L), (s: Long, t: Long, w: Double) => Result.FOLLOW, Aggregator.NO_AGGREGATION, DfsBaseConfig.NO_MAX_DEPTH, ProgressTracker.NULL_TRACKER).compute.toArray

    val count: Int = result.length
    for (cursor <- 0 until count) {
      println(nodeResult(result(cursor).toInt).values + ":" + result(cursor))
    }

    println(result.mkString("-"))


  }


  @Test
  def idmap(): Unit = {
    val ids: IdMap = hg.idMap()

    println(s"ids.len:${ids.nodeCount().toInt}")

    for (i <- 0 until(ids.nodeCount().toInt)){
      println(s"id:${i},mapid:${ids.toOriginalNodeId(i.toLong)},mmapid:${ids.toMappedNodeId(ids.toOriginalNodeId(i.toLong))}")
    }

    println("cursor\t" + "id.map\t" + "mapId\t" + "originalId\t" + "community\t")
    for (elem <- 0 until hg.idMap().nodeCount().toInt) {
      val mapId = ids.toMappedNodeId(nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      })
      val originalId = nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      }
      println(elem + "\t\t" + ids.toMappedNodeId(elem) + "\t\t" + mapId + "\t\t" + originalId)
    }
  }


  @Test
  def DFS1Test(): Unit = {
    val l = hg.toMappedNodeId(1L)
    val targetList = List(4L)

    val result: Array[Long] = PandaDFSConfig.DFS(hg,l,targetList)

    val count: Int = result.length
    for (cursor <- 0 until count) {
      println(nodeResult(result(cursor).toInt).values + ":" + result(cursor))
    }

    println(result.mkString("-"))

    val mapListBuffer = ListBuffer[LynxValue]()
    for (cursor <- 0 until count) {
      mapListBuffer += LynxValue(nodeResult(cursor).values.toList)
    }

    val mapList: immutable.Seq[LynxValue] = mapListBuffer.toList
    println(LynxValue(mapList))



  }
}
