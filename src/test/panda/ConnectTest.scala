package panda

import org.grapheco.pandadb.GraphDataBaseBuilder;

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
        |match (n)
        |with n as nodes
        |match ()-[r]-()
        |with r as rels
        |return nodes, rels
        |""".stripMargin)
    result.show()
    tx.commit()
    tx.close()
    db.close()
  }


}
