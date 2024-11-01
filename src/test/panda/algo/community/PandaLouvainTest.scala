package panda.algo.community

import org.cai.algoconfig.community.PandaLouvainConfig
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxMap
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.termination.TerminationFlag

import scala.collection.mutable.ListBuffer;

/**
 * @author cai584770
 * @date 2024/7/10 10:48
 * @Version
 */
class PandaLouvainTest {
  private val dbPath = "/home/cjw/db/louvain.db"
  private val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "User", "LINK")
  private val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("LINK"))

  @Test
  def louvainTestFunc():Unit={
    val (idMap, nodeIdMap, nodeIdInverseMap) = GraphConversion.getIdMap(nodeResult)
    val hugeGraph = GraphConversion.createHugeGraph(relationshipResult, idMap, nodeIdInverseMap, "LINK")

    val (dendrogram, modularities) = PandaLouvainConfig.louvain(hugeGraph)
    val count: Int = hugeGraph.idMap().nodeCount().toInt
    val result = dendrogram(0).toArray

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeIdMap.getOrElse(cursor, -1)).toString -> LynxValue(result(cursor)))
      mapListBuffer += map
    }

    Map("Modularities" -> LynxValue(modularities))

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    println("---")
    println(LynxValue(mapList.map(LynxMap)))
  }

  @Test
  def louvainTest(): Unit = {
    val (dendrogram,modularities) = PandaLouvainConfig.louvain(hg, TOLERANCE_DEFAULT, 10, includeIntermediateCommunities = true, 1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)

    val result = dendrogram(0).toArray

    for (i <-  0 until hg.idMap().nodeCount().toInt) {
      println(nodeResult(i).values+":"+result(i))
    }
  }


  @Test
  def louvainConfigTest(): Unit = {
    val (dendrogram, modularities) = PandaLouvainConfig.louvain(hg)

    val count: Int = hg.idMap().nodeCount().toInt
    val result = dendrogram(0).toArray

    val mapListBuffer = ListBuffer[Map[String, LynxValue]]()
    for (cursor <- 0 until count) {
      val map: Map[String, LynxValue] = Map(LynxValue(nodeResult(cursor).values.toList).toString -> LynxValue(result(cursor)))
      mapListBuffer += map
    }

    val mapList: List[Map[String, LynxValue]] = mapListBuffer.toList
    LynxValue(mapList.map(LynxMap))

    println(LynxValue(mapList.map(LynxMap)))

  }



}
