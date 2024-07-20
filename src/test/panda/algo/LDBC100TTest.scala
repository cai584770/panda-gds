package panda.algo

import org.cai.pandadb.algoconfig.{PandaLabelPropagationConfig, PandaLouvainConfig, PandaPageRankConfig}
import org.cai.pandadb.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.api.IdMap

import java.util.function.LongToDoubleFunction

/**
 * @author cai584770
 * @date 2024/7/18 10:13
 * @Version
 */
class LDBC100TTest {
  private val dbPath = "/home/cjw/ldbc100t.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")
  private val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

  @Test
  def louvainLDBCTest(): Unit = {
    val t1 = System.nanoTime()
    val (dendrogram, modularities) = PandaLouvainConfig.louvain(hg)
    println(System.nanoTime()-t1)
    println(dendrogram.length)

    for (i <- 0 until (10)) {
      System.out.println("dendrogram[" + i + "]:" + dendrogram(i))
      System.out.println("modularities[" + i + "]:" + modularities(i))
    }

  }

  @Test
  def lpTest(): Unit = {
    val t1 = System.nanoTime()
    val propagation: Array[Long] = PandaLabelPropagationConfig.labelPropagation(hg)
    println(System.nanoTime()-t1)

    val ids: IdMap = hg.idMap()
    println("cursor\t" + "id.map\t" + "mapId\t" + "originalId\t" + "community\t")

    for (elem <- propagation.indices) {
      val mapId = ids.toMappedNodeId(nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      })
      val originalId = nodeResult(elem).values.head.value match {
        case pn: PandaNode => pn.longId
      }
      println(elem + "\t\t" + ids.toMappedNodeId(elem) + "\t\t" + mapId + "\t\t" + originalId + "\t\t" + propagation(elem))
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
  def pageRankTest(): Unit = {
    val t1 = System.nanoTime()
    val pageRankResult: LongToDoubleFunction = PandaPageRankConfig.pageRank(hg)
    println(System.nanoTime()-t1)

    println(pageRankResult)

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until 10) {
      println(pageRankResult.applyAsDouble(cursor.toLong))
    }

  }



}
