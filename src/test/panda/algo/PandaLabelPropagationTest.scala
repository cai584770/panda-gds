package panda.algo

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.api.IdMap
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.labelpropagation.{LabelPropagation, LabelPropagationStreamConfigImpl}

/**
 * @author cai584770
 * @date 2024/7/12 10:49
 * @Version
 */
class PandaLabelPropagationTest {

  private val dbPath = "/home/cjw/lp.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "FOLLOW")

  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("FOLLOW"))

  @Test
  def lpTest(): Unit = {
    val config: LabelPropagationStreamConfigImpl.Builder = new LabelPropagationStreamConfigImpl.Builder

    config.concurrency(1).maxIterations(1).nodeWeightProperty(null).seedProperty(null)

    val propagation: Array[Long] = new LabelPropagation(hg, config.build(), DefaultPool.INSTANCE, ProgressTracker.NULL_TRACKER).compute().labels.toArray()

    println(propagation.mkString)

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



}
