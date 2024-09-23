package panda.algo.community

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.haa.HugeAtomicLongArray
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.triangle.{IntersectingTriangleCount, TriangleCountBaseConfigImpl, TriangleCountResult}

import java.util.{Map => JMap}

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

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

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

    val array: HugeAtomicLongArray = compute.localTriangles()

    val nodecount = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until nodecount) {
      println(nodeResult(cursor).values + ":" + compute.localTriangles().get(cursor))
    }

  }

  @Test
  def testWithFunction: Unit = {
    val dbPath = "/home/cjw/wcc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("TYPE"))


  }
}
