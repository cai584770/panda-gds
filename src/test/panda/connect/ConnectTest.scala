package panda.connect

import org.grapheco.pandadb.GraphDataBaseBuilder

/**
 * @author cai584770
 * @date 2024/7/8 10:33
 * @Version
 */
object ConnectTest {

  def main(args: Array[String]): Unit = {
    val path = "/home/cjw/lsbc.db"
    val db = GraphDataBaseBuilder.newEmbeddedDatabase(path)
    val tx = db.beginTransaction()
    val result = tx.executeQuery(
      """
        |match (n:Person)
        |return n
        |limit 10
        |""".stripMargin)
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }


}
