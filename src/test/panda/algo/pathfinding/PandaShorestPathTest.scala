package panda.algo.pathfinding

import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.structural.{LynxNode, LynxPath}
import org.grapheco.pandadb.{GraphDataBaseBuilder, PandaInstanceContext}
import org.grapheco.pandadb.facade.{GraphDatabaseService, GraphFacade, PandaTransaction}
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType

/**
 * @author cai584770
 * @date 2024/9/24 12:49
 * @Version
 */
class PandaShorestPathTest {
  private val dbPath = "/home/cjw/db/dstsp.db"

  @Test
  def shorestPathTest():Unit={
    val db: GraphDatabaseService = GraphDataBaseBuilder.newEmbeddedDatabase(dbPath)
    val tx: PandaTransaction = db.beginTransaction()

    val s = 0
    val e = 2
    val source = 1
    val target = 4

    val query: String =
      s"""
         |match p = (m)-[$s..$e]-(n)
         |where id(m)=$source and id(n)=$target
         |return p
         |""".stripMargin
    val nodeRecords: Iterator[LynxRecord] = tx.executeQuery(query).records()
    val r: List[LynxPath] = nodeRecords.map(x => x.values).map(x => x.asInstanceOf[LynxPath]).toList
    val n: List[List[LynxNode]] = r.map(x => x.nodes)
    val shortestList: List[LynxNode] = n.minBy(_.length)

    println(shortestList.mkString("\n"))

    println(LynxValue(shortestList))


  }


}
