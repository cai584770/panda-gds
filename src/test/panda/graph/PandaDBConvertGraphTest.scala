package panda.graph

import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.types.LynxValue
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.loading.construction.{GraphFactory, NodeLabelToken, NodeLabelTokens, NodesBuilder}
import org.neo4j.values.storable.{Value, Values}

import scala.collection.JavaConverters._

/**
 * @author cai584770
 * @date 2024/7/9 16:03
 * @Version
 */
class PandaDBConvertGraphTest {

  @Test
  def convertNodeTest(): Unit = {
    val dbPath = "/home/cjw/ppr.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val nodesCount: Int = nodeResult.length
    val lastNode: LynxRecord = nodeResult.last
    val maxNodeId: Long = lastNode.values.head.value match {
      case pn: PandaNode => pn.longId
      case _ => println("Max Node Id Not Found!")
        0L
    }

    val nodesBuilder: NodesBuilder = GraphFactory.initNodesBuilder.nodeCount(nodesCount).maxOriginalId(maxNodeId).concurrency(1).hasLabelInformation(true).build


    nodeResult.foreach {
      n =>
        val record = n.values.head.value
        println(record)
        record match {
          case pn: PandaNode =>
            val gdsId: Long = pn.longId

            val properties: Seq[(String, LynxValue)] = pn.props
            val gdsProperties: Map[String, Value] = properties.foldLeft(Map.empty[String, Value]) { (acc, property) =>
              val key: String = property._1
              val value = property._2.value
              acc + (key -> Values.of(value))
            }

            val gdsPropertiesJava: java.util.Map[String, org.neo4j.values.storable.Value] = gdsProperties.asJava

            println(gdsPropertiesJava)

            val labels: Array[String] = pn.labels.map(_.value).toArray
            val gdsNodeLabelTokens: NodeLabelToken = NodeLabelTokens.ofNullable(labels)

            nodesBuilder.addNode(gdsId, gdsPropertiesJava, gdsNodeLabelTokens)
          case _ => println("Panda Node?")
        }
    }

    val l = nodesBuilder.importedNodes()
    println(l)

    val nodes = nodesBuilder.build()
//
//    println(nodes.idMap().nodeCount())


  }

  @Test
  def convertNode1Test(): Unit = {
    val dbPath = "/home/cjw/ppr.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg: HugeGraph = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("TYPE"))


  }


  @Test
  def convertGraphTest(): Unit = {
    val dbPath = "/home/cjw/ldbc100t.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

  }


}
