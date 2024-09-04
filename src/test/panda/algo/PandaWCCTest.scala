package panda.algo

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.utils.paged.dss.DisjointSetStruct
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.wcc.{WccAlgorithmFactory, WccBaseConfig, WccMutateConfigImpl}

import java.util.{Map => JMap}
import java.util
import java.util.Map
import scala.collection.mutable

/**
 * @author cai584770
 * @date 2024/9/2 9:20
 * @Version
 */
class PandaWCCTest {

  @Test
  def testOutFunction:Unit={
    val dbPath = "/home/cjw/wcc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("TYPE"))

    val javaMap: JMap[String, Object] = JMap.of(
      "threshold", 3.14.asInstanceOf[AnyRef],
      "relationshipWeightProperty", "threshold",
      "mutateProperty", "resultProperty"
    )

    val config: WccMutateConfigImpl = new WccMutateConfigImpl(CypherMapWrapper.create(javaMap))

    val result: DisjointSetStruct = new WccAlgorithmFactory[WccBaseConfig]().build(hg, config, ProgressTracker.NULL_TRACKER).compute

    println(result)
    val communityData: Array[Long] = new Array[Long](hg.nodeCount().toInt)
    hg.forEachNode { nodeId =>
      communityData(nodeId.toInt) = result.setIdOf(nodeId)
      true
    }

    println(communityData.mkString(","))

  }

  @Test
  def testWithFunction: Unit = {
    val dbPath = "/home/cjw/wcc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("TYPE"))




  }

}
