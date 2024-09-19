package neo4j.connect

import org.junit.jupiter.api.Test
import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Record, Result, Session, Transaction}
import org.neo4j.driver.types.Node

/**
 * @author cai584770
 * @date 2024/9/17 12:04
 * @Version
 */
class ConnectTest {

  @Test
  def t1(): Unit = {
    val uri = "bolt://localhost:7687"
    val user = "neo4j"
    val password = "cai584770"
    val driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

    val session: Session = driver.session

    try {
      val transaction: Transaction = session.beginTransaction()

      val result: Result = transaction.run("MATCH (n:Person) RETURN n;")


      while (result.hasNext) {
        val record = result.next()
        val node: Node = record.get("n").asNode()

        println(s"Node ID: ${node.id()}, Name: ${node.get("name").asString()}, Age: ${node.get("age").asInt()}")

      }

    } finally {
      session.close()
      driver.close()
    }

  }

}
