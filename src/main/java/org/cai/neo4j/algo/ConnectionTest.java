package org.cai.neo4j.algo;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

/**
 * @author cai584770
 * @date 2024/7/1 11:47
 * @Version
 */
public class ConnectionTest {
    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "cai584770";

        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

        try (Session session = driver.session()) {
            Result result = session.run("MATCH (n) RETURN n LIMIT 10");

            while (result.hasNext()) {
                Record record = result.next();
                Node node = record.get("n").asNode();
                long nodeId = node.id();
                System.out.println("Node ID: " + nodeId);
            }
        } finally {
            driver.close();
        }


    }

}
