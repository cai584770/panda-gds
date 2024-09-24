package panda.algo.community

import org.cai.algoconfig.community.PandaWCCConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxMap
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.utils.paged.dss.DisjointSetStruct
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.wcc.{WccAlgorithmFactory, WccBaseConfig, WccMutateConfigImpl}

import java.util.{Map => JMap}
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/9/2 9:20
 * @Version
 */
class PandaWCCTest {
  private val dbPath = "/home/cjw/db/wcc.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "LINK")
  private val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("LINK"))

  @Test
  def WccTest:Unit={
    val javaMap: JMap[String, Object] = JMap.of(
      "threshold", 3.14.asInstanceOf[AnyRef],
      "relationshipWeightProperty", "threshold",
      "mutateProperty", "resultProperty"
    )

    val config: WccMutateConfigImpl = new WccMutateConfigImpl(CypherMapWrapper.create(javaMap))

    val result: DisjointSetStruct = new WccAlgorithmFactory[WccBaseConfig]().build(hg, config, ProgressTracker.NULL_TRACKER).compute

    val nodecount = hg.idMap().nodeCount().toInt

    val communityData: Array[Long] = new Array[Long](nodecount)
    hg.forEachNode { nodeId =>
      communityData(nodeId.toInt) = result.setIdOf(nodeId)
      true
    }

    for (cursor <- 0 until nodecount) {
      println(nodeResult(cursor).values + ":" + communityData(cursor))
    }

  }

  @Test
  def testWithFunction: Unit = {
    val longs: Array[Long] = PandaWCCConfig.wcc(hg)

    val count: Int = hg.idMap().nodeCount().toInt

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeResult(cursor).values.toList).toString -> LynxValue(longs(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))
    println(LynxValue(mapList.map(LynxMap)))
  }

}
