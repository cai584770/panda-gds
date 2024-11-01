package panda.convert

import org.junit.jupiter.api.Test
import org.neo4j.gds.collections.haa.HugeAtomicLongArray
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.triangle.{IntersectingTriangleCount, IntersectingTriangleCountFactory, TriangleCountBaseConfigImpl, TriangleCountResult, TriangleCountStatsProc, TriangleStream}

import java.util.{Map => JMap}

/**
 * @author cai584770
 * @date 2024/10/30 16:48
 * @Version
 */
class GraphCovertTest {

  @Test
  def convert(): Unit = {
    val dbPath = "/home/cjw/db/tc.db"
    val nodeLabel = "Person"
    val relationShipLabel = "KNOWS"

    val (hugeGraph,nodeIdMap,nodeIdInverseMap) = GraphCovert.convertWithId(dbPath, nodeLabel, relationShipLabel)
//    println(hugeGraph.nodeCount())
//    println(hugeGraph.relationshipCount())

    println("-----------------")
    //    val javaMap: JMap[String, Object] = null
    //      "threshold", 3.14.asInstanceOf[AnyRef],
    //      "relationshipWeightProperty", "threshold",
    //      "mutateProperty", "resultProperty",
    val javaMap: JMap[String, Object] = JMap.of(
      "writeProperty", "triangleCount"
//      "orientation", "UNDIRECTED"
    )
    val wrapper: CypherMapWrapper = CypherMapWrapper.create(javaMap)

    val triangleCountConfig: TriangleCountBaseConfigImpl = new TriangleCountBaseConfigImpl(wrapper)

    val compute: TriangleCountResult = IntersectingTriangleCount.create(hugeGraph, triangleCountConfig, DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute

    println("--------------local--------------------")
    val arrayLocal: HugeAtomicLongArray = compute.localTriangles()
    val nodecount = hugeGraph.idMap().nodeCount().toInt
    for (cursor <- 0 until nodecount) {
      println(nodeIdMap.getOrElse(cursor,-1) + ":" + arrayLocal.get(cursor))
    }

    println("--------------global--------------------")
    val arrayGlobal: Long = compute.globalTriangles()
    println(arrayGlobal)

    println("-----------")
    val stream: TriangleStream = TriangleStream.create(hugeGraph, DefaultPool.INSTANCE, 4)

    val values = stream.compute().toArray()
    println(values.getClass)

    for (value <- values){
      value match {
        case re: TriangleStream.Result =>
          println(s"re.nodeA: ${re.nodeA},re.nodeB: ${re.nodeB},re.nodeC: ${re.nodeC}")
        case _ =>
          println("Unknown type")
      }

    }

  }

}
