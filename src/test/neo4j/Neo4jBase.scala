package neo4j

import org.junit.jupiter.api.Test
import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Session, SessionConfig}

/**
 * @author cai584770
 * @date 2024/10/30 9:25
 * @Version
 */
class Neo4jBase {

  @Test
  def connect(): Unit = {
    val driver: Driver = GraphDatabase.driver(ConnectBase.uri, AuthTokens.none())
    val session: Session = driver.session(SessionConfig.forDatabase("neo4j"))

    try {
      val result = session.run("MATCH (n) RETURN id(n) as nodeId, n.name as name ;")
      while (result.hasNext) {
        val record = result.next()
        val nodeId = record.get("nodeId").asInt()
        val name = record.get("name").asString()

        println(s"Node ID: $nodeId, Storage: $name")
      }
    } catch {
      case e: Exception =>
        println(s"An error occurred: ${e.getMessage}")
    } finally {
      session.close()
      driver.close()
    }
  }

  @Test
  def clearDatabase(): Unit = {
    val driver: Driver = GraphDatabase.driver(ConnectBase.uri, AuthTokens.none())
    val session: Session = driver.session(SessionConfig.forDatabase("neo4j"))

    try {
      session.run("MATCH (n) DETACH DELETE n")
      println("All nodes and relationships have been deleted.")

      val indexResult = session.run("SHOW INDEXES")
      while (indexResult.hasNext) {
        val indexRecord = indexResult.next()
        val indexName = indexRecord.get("name").asString()
        session.run(s"DROP INDEX $indexName")
        println(s"Index '$indexName' has been deleted.")
      }

      println("All indexes have been deleted.")
    } catch {
      case e: Exception =>
        println(s"An error occurred: ${e.getMessage}")
    } finally {
      session.close()
      driver.close()
    }
  }



}
