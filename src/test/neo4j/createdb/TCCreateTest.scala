package neo4j.createdb

import neo4j.ConnectBase
import org.junit.jupiter.api.Test
import org.neo4j.driver.{AuthTokens, Driver, GraphDatabase, Session, SessionConfig}

/**
 * @author cai584770
 * @date 2024/10/30 9:44
 * @Version
 */
class TCCreateTest {

  @Test
  def create(): Unit = {
    val driver: Driver = GraphDatabase.driver(ConnectBase.uri, AuthTokens.none())
    val session: Session = driver.session(SessionConfig.forDatabase("neo4j"))

    try {
      session.run("MATCH (n) DETACH DELETE n")
      println("Database cleared.")

      val createQuery =
        """
          |CREATE
          |  (alice:Person {name: 'Alice'}),
          |  (michael:Person {name: 'Michael'}),
          |  (karin:Person {name: 'Karin'}),
          |  (chris:Person {name: 'Chris'}),
          |  (will:Person {name: 'Will'}),
          |  (mark:Person {name: 'Mark'}),
          |
          |  (michael)-[:KNOWS]->(karin),
          |  (michael)-[:KNOWS]->(chris),
          |  (will)-[:KNOWS]->(michael),
          |  (mark)-[:KNOWS]->(michael),
          |  (mark)-[:KNOWS]->(will),
          |  (alice)-[:KNOWS]->(michael),
          |  (will)-[:KNOWS]->(chris),
          |  (chris)-[:KNOWS]->(karin)
          |""".stripMargin

      session.run(createQuery)
      println("Nodes and relationships created.")

      val matchDB = "MATCH (n)-[r]->(m) RETURN n, r, m"
      val result = session.run(matchDB)

      while (result.hasNext) {
        val record = result.next()

        val nodeN = record.get("n").asNode()
        val nodeNId = nodeN.id()

        val relationship = record.get("r").asRelationship()
        val relationshipType = relationship.`type`()

        val nodeM = record.get("m").asNode()
        val nodeMId = nodeM.id()

        println(s"Node N - ID: $nodeNId")
        println(s"Relationship - Type: $relationshipType")
        println(s"Node M - ID: $nodeMId")
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
