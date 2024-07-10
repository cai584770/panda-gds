package org.cai.neo4j.algo;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.*;

/**
 * @author cai584770
 * @date 2024/7/3 16:47
 * @Version
 */
public class ConvertGraphTest {

    public static void main(String[] args) {
        try (Session session = ConnectConfig.driver.session()) {
            session.writeTransaction(new TransactionWork<Void>() {
                @Override
                public Void execute(Transaction tx) {
                    Result checkResult = tx.run("CALL gds.graph.exists('personGraph') YIELD exists RETURN exists");
                    if (checkResult.hasNext() && checkResult.next().get("exists").asBoolean()) {
                        tx.run("CALL gds.graph.drop('personGraph')");
                    }
                    return null;
                }
            });

            Map<String, Object> nodeProjection = new HashMap<>();
            Map<String, Object> relationshipProjection = new HashMap<>();

            Map<Long, Node> nodes = new HashMap<>();
            Result nodeResult = session.run("MATCH (n:Person) RETURN n");
            while (nodeResult.hasNext()) {
                Record record = nodeResult.next();
                Node neo4jNode = record.get("n").asNode();
                long id = neo4jNode.id();
                Iterable<String> labels = neo4jNode.labels();
                Map<String, Object> properties = neo4jNode.asMap();
                nodes.put(neo4jNode.id(), neo4jNode);
                nodeProjection.put("label", labels.iterator().next());
                nodeProjection.put("properties", properties);
            }

            List<Relationship> neo4jEdges = new ArrayList<>();
            Result edgeResult = session.run("MATCH (n:Person)-[r:KNOWS]->(m:Person) RETURN r");
            while (edgeResult.hasNext()) {
                Record record = edgeResult.next();
                Relationship neo4jEdge = record.get("r").asRelationship();
                neo4jEdges.add(neo4jEdge);
                Map<String, Object> relationshipDetails = new HashMap<>();
                relationshipDetails.put("type", neo4jEdge.type());
                relationshipDetails.put("orientation", "NATURAL");
                relationshipDetails.put("aggregation", "DEFAULT");
                relationshipDetails.put("indexInverse", false);
                relationshipDetails.put("properties", neo4jEdge.asMap());
                relationshipProjection.put(neo4jEdge.type(), relationshipDetails);
            }

            Map<String, Object> configuration = Collections.emptyMap();

            System.out.println("nodeProjection.size(): " + nodeProjection.size());
            System.out.println("relationshipProjection.size(): " + relationshipProjection.size());
            System.out.println("nodes.size(): " + nodes.size());
            System.out.println("neo4jEdges.size(): " + neo4jEdges.size());

        } finally {
            ConnectConfig.driver.close();
        }
    }
}
