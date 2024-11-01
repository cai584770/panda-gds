package neo4j.algo

import neo4j.ConnectBase
import org.junit.jupiter.api.Test
import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Session, SessionConfig}

/**
 * @author cai584770
 * @date 2024/10/30 10:15
 * @Version
 */
class TCTest {

  @Test
  def tcTest(): Unit = {

    val driver: Driver = GraphDatabase.driver(ConnectBase.uri, AuthTokens.none())
    val session: Session = driver.session(SessionConfig.forDatabase("neo4j"))

    try {
      val createSubGraph =
        """
          |CALL gds.graph.project(
          |  'myGraph',
          |  'Person',
          |  {
          |    KNOWS: {
          |      orientation: 'UNDIRECTED'
          |    }
          |  }
          |);
          |""".stripMargin
      session.run(createSubGraph)

      val tc =
        """
          |CALL gds.triangleCount.stats('myGraph')
          |YIELD globalTriangleCount, nodeCount;
          |""".stripMargin
      val result = session.run(tc)

      while (result.hasNext) {
        val record = result.next()
        val globalTriangleCount = record.get("globalTriangleCount").asInt()
        val nodeCount = record.get("nodeCount").asInt()

        println(s"globalTriangleCount: $globalTriangleCount, nodeCount: $nodeCount")
      }

    } catch {
      case e: Exception =>
        println(s"An error occurred: ${e.getMessage}")
    } finally {
      session.close()
      driver.close()
    }

  }

}
