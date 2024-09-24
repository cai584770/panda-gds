package panda.algo.community

import org.cai.algoconfig.community.PandaLabelPropagationConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxMap
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.api.IdMap
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.labelpropagation.{LabelPropagation, LabelPropagationStreamConfigImpl}

import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/12 10:49
 * @Version
 */
class PandaLabelPropagationTest {

  private val dbPath = "/home/cjw/db/lp.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "FOLLOW")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("FOLLOW"))

  @Test
  def lpTest(): Unit = {
    val config: LabelPropagationStreamConfigImpl.Builder = new LabelPropagationStreamConfigImpl.Builder

    config.concurrency(1).maxIterations(10).nodeWeightProperty(null).seedProperty(null).minCommunitySize(1)

    val propagation: Array[Long] = new LabelPropagation(hg, config.build(), DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute().labels.toArray()

    for (cursor <- 0 until hg.idMap().nodeCount().toInt) {
      println(nodeResult(cursor).values + ":" + propagation(cursor))
    }

  }

  @Test
  def lpTest1(): Unit = {
    val config: LabelPropagationStreamConfigImpl.Builder = new LabelPropagationStreamConfigImpl.Builder

    config.concurrency(1).maxIterations(10).nodeWeightProperty(null)

    val propagation: Array[Long] = new LabelPropagation(hg, config.build(), DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute().labels.toArray

    val ids: IdMap = hg.idMap()
    println("cursor\t" + "id.map\t"+ "mapId\t" + "originalId\t"+ "community\t")

    for (elem <- propagation.indices) {
      val mapId = ids.toMappedNodeId(nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      })
      val originalId = nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      }
      println(elem + "\t\t" + ids.toMappedNodeId(elem)+ "\t\t" + mapId+ "\t\t" + originalId + "\t\t" + propagation(elem))
    }

    println("mapId\t" + "originalId\t" + "community\t")

    for (elem <- propagation.indices) {
      val mapId = ids.toMappedNodeId(nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      })
      val originalId = nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      }
      println(mapId + "\t\t" + originalId + "\t\t" + propagation(elem))
    }

  }

  @Test
  def lp2Test(): Unit = {
    val lpResult: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hg)
    val count: Int = hg.idMap().nodeCount().toInt

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeResult(cursor).values.toList).toString -> LynxValue(lpResult(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))

    println(LynxValue(mapList.map(LynxMap)))

  }

}
