package panda.algo.Centrality

import org.cai.algoconfig.centrality.PandaBetweennessCentralityConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.{LynxList, LynxMap}
import org.grapheco.pandadb.graph.PandaNode
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.haa.HugeAtomicDoubleArray

import scala.collection.immutable
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/7/20 10:50
 * @Version
 */
class PandaBetweennessCentralityTest {

  private val dbPath = "/home/cjw/db/bc.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "FOLLOWS")
  private val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("FOLLOWS"))

  @Test
  def PBCTest():Unit={
    val result: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hg)
    val le = hg.idMap().nodeCount().toInt
    println(nodeResult(0).values.getClass)
    println(nodeResult(0).cols.getClass)

    for (l <- 0 until le){
      println(nodeResult(l).values + ":" +result.get(l.toLong))
    }

  }

  @Test
  def PBCLynxValueTest(): Unit = {
    val bcResult: HugeAtomicDoubleArray = PandaBetweennessCentralityConfig.betweennessCentrality(hg)

    val mapListBuffer = ListBuffer[Map[Any, Double]]()

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until count) {
      mapListBuffer += Map(nodeResult(cursor).values->bcResult.get(cursor.toLong))
    }

    println(mapListBuffer.toList)


    val lynxValue: LynxValue = LynxValue.apply(mapListBuffer.toList)
    println(lynxValue)

  }




}
