package panda.algo.community

import org.cai.algoconfig.community.PandaTriangleCountConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.{LynxList, LynxMap}
import org.grapheco.lynx.types.property.LynxInteger
import org.grapheco.lynx.types.structural.LynxNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.algorithms.community.CommunityAlgorithmsFacade
import org.neo4j.gds.api.schema.{GraphSchema, ImmutableMutableGraphSchema, MutableRelationshipSchema, RelationshipSchema}
import org.neo4j.gds.api.{IdMap, RelationshipConsumer}
import org.neo4j.gds.collections.haa.HugeAtomicLongArray
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.{ProgressTracker, Task}
import org.neo4j.gds.triangle.{IntersectingTriangleCount, IntersectingTriangleCountFactory, TriangleCountBaseConfigImpl, TriangleCountResult, TriangleCountStreamConfig, TriangleStream}

import java.util
import java.util.{Map => JMap}
import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/9/2 9:22
 * @Version
 */
class PandaTriangleCountingTest {

  @Test
  def tcTest: Unit = {
    val dbPath = "/home/cjw/db/tc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")
    val hg: HugeGraph = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

    println(hg.nodeCount().toInt)
    println(hg.relationshipCount().toInt)


    //    val javaMap: JMap[String, Object] = JMap.of(
    //      "threshold", 3.14.asInstanceOf[AnyRef],
    //      "relationshipWeightProperty", "threshold",
    //      "mutateProperty", "resultProperty"
    //    )
    val javaMap: JMap[String, Object] = null
    val wrapper: CypherMapWrapper = CypherMapWrapper.create(javaMap)

    val triangleCountConfig: TriangleCountBaseConfigImpl = new TriangleCountBaseConfigImpl(wrapper)

    val compute: TriangleCountResult = IntersectingTriangleCount.create(hg, triangleCountConfig, DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute

    println("--------------local--------------------")
    val arrayLocal: HugeAtomicLongArray = compute.localTriangles()
    val nodecount = hg.idMap().nodeCount().toInt

    for (cursor <- 0 until nodecount) {
      println(nodeResult(cursor).values + ":" + arrayLocal.get(cursor))
    }

    println("--------------global--------------------")
    val arrayGlobal: Long = compute.globalTriangles()
    println(arrayGlobal)

    println("--------------IntersectingTriangleCount--------------------")
    val task: IntersectingTriangleCount = new IntersectingTriangleCountFactory().build(hg, triangleCountConfig, ProgressTracker.NULL_TRACKER)
    val result = task.compute()
    println(result.localTriangles())
    println(result.globalTriangles())

    println("-----------")
    //    val d: Double = hg.relationshipProperty(1L, 2L)
    val stream: TriangleStream = TriangleStream.create(hg, DefaultPool.INSTANCE, 4)

    val value = stream.compute()
    value.forEach(println)

    println(hg.isMultiGraph)
    println(hg.hasRelationshipProperty)
    val d: Double = hg.relationshipProperty(2L, 4L, 0.00)
    println(d)

    println("-----------")
    println(hg.exists(2L, 3L))
    println(hg.exists(2L, 4L))
    println(hg.exists(5L, 2L))
    println(hg.exists(6L, 5L))
    println(hg.exists(1L, 2L))
    println(hg.exists(5L, 4L))
    println(hg.exists(4L, 3L))
    println(hg.exists(6L, 2L))
  }

  @Test
  def testWithFunction: Unit = {
    val dbPath = "/home/cjw/db/tc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val (idMap, nodeIdMap, nodeIdInverseMap) = GraphConversion.getIdMap(nodeResult)

    val hugeGraph = GraphConversion.createHugeGraph(relationshipResult, idMap, nodeIdInverseMap, "KNOWS")

    val (global, local) = PandaTriangleCountConfig.triangleCountStats(hugeGraph)
    val lists = PandaTriangleCountConfig.triangleCountStream(hugeGraph)
    val nodecount = idMap.nodeCount().toInt
//    println("----------------- global -----------------")
//    println(s"globalTriangleCount: $global, nodeCount: ${idMap.nodeCount()}")
//    val map: LynxMap = LynxMap.apply(Map("GlobalTriangleCount" -> LynxInteger.apply(global), "NodeCount" -> LynxInteger.apply(idMap.nodeCount())))
//    println(map)
//
//    println("----------------- local -----------------")
//    val lovalResult = ListBuffer[LynxInteger]()
//    val nodeList = ListBuffer[LynxValue]()
//    for (cursor <- 0 until nodecount) {
////      println(nodeIdMap.getOrElse(cursor, -1) + ":" + nodeResult(cursor) +":" + local.get(cursor))
////val values: Seq[LynxValue] = nodeResult(cursor).values
//      lovalResult += LynxInteger.apply(local.get(cursor))
//      val value: LynxValue = LynxValue(nodeResult(cursor).values.toList)
//      nodeList += value
//    }
////    val list1: immutable.Seq[List[LynxValue]] = nodeList.toList
////    LynxList.apply(list1)
//
//    println(LynxList.apply(lovalResult.toList))
//    println(LynxMap(Map("TriangleCountNodeList" -> LynxList.apply(nodeList.toList))))
//    println(LynxMap(Map("LocalTriangleCount" -> LynxList.apply(lovalResult.toList))))
//
//    println(LynxList.apply(List(LynxMap(Map("TriangleCountNodeList" -> LynxList.apply(nodeList.toList))),LynxMap(Map("LocalTriangleCount" -> LynxList.apply(lovalResult.toList))))))


    println("----------------- stream -----------------")
    val streamResultLynxList = ListBuffer[LynxList]()
    for (list <- lists) {

      streamResultLynxList += LynxList.apply(List(LynxInteger.apply(nodeIdMap.getOrElse(list(0).toInt, -1)), LynxInteger.apply(nodeIdMap.getOrElse(list(1).toInt, -1)), LynxInteger.apply(nodeIdMap.getOrElse(list(2).toInt, -1))))

    }

    println(LynxList.apply(streamResultLynxList.toList))

  }

  @Test
  def testEncapsulation: Unit = {
    val dbPath = "/home/cjw/db/tc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val (idMap, nodeIdMap, nodeIdInverseMap) = GraphConversion.getIdMap(nodeResult)

    val hugeGraph = GraphConversion.createHugeGraph(relationshipResult, idMap, nodeIdInverseMap, "KNOWS")

    val (global, local) = PandaTriangleCountConfig.triangleCountStats(hugeGraph)
    val lists = PandaTriangleCountConfig.triangleCountStream(hugeGraph)
    val nodecount = idMap.nodeCount().toInt
    println("----------------- global -----------------")
    println(s"globalTriangleCount: $global, nodeCount: ${idMap.nodeCount()}")

    println("----------------- local -----------------")

    for (cursor <- 0 until nodecount) {
      println(nodeIdMap.getOrElse(cursor, -1) + ":" + nodeResult(cursor) + ":" + local.get(cursor))
    }

    println("----------------- stream -----------------")
    for(list <- lists){
      println(list.mkString(" "))
    }



  }


}
