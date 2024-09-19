package panda.connect

import org.grapheco.pandadb.GraphDataBaseBuilder
import org.grapheco.pandadb.driver.PandaDBDriver
import org.junit.jupiter.api.Test

/**
 * @author cai584770
 * @date 2024/7/8 10:16
 * @Version
 */
class ConnectPanda {
  val host = "10.0.82.148"
  val port = 7600
  val panda: PandaDBDriver = new PandaDBDriver(host, port)

  @Test
  def connectTest():Unit={
    val path = "/home/cjw/db/test.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery(
      """
        |match (n)
        |return n
        |limit 10
        |""".stripMargin)
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }

}
