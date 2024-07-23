package neo4j.algo;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.gds.NodeLabel;
import org.neo4j.gds.RelationshipType;
import org.neo4j.gds.api.*;
import org.neo4j.gds.api.properties.graph.GraphPropertyStore;
import org.neo4j.gds.api.properties.nodes.ImmutableNodePropertyStore;
import org.neo4j.gds.api.properties.nodes.NodeProperty;
import org.neo4j.gds.api.properties.nodes.NodePropertyStore;
import org.neo4j.gds.api.schema.*;
import org.neo4j.gds.core.io.file.csv.CapabilitiesDTO;
import org.neo4j.gds.core.loading.*;

import java.util.*;

/**
 * @author cai584770
 * @date 2024/7/4 16:53
 * @Version
 */
public class GDSGraphTest {

    public static void main(String[] args) {
        // Neo4j connection setup
        try (Session session = ConnectConfig.driver.session()) {

            // Import nodes from Neo4j
            List<Relationship> neo4jEdges = new ArrayList<>();
            Map<Long, Node> nodes = new HashMap<>();

            Result nodeResult = session.run("MATCH (n:Person) RETURN n");
            while (nodeResult.hasNext()) {
                Record record = nodeResult.next();
                Node neo4jNode = record.get("n").asNode();
                nodes.put(neo4jNode.id(), neo4jNode);
                printNodeInfo(neo4jNode);
            }

            // Import relationships from Neo4j
            Result edgeResult = session.run("MATCH (n:Person)-[r:KNOWS]->(m:Person) RETURN r");
            while (edgeResult.hasNext()) {
                Record record = edgeResult.next();
                Relationship neo4jEdge = record.get("r").asRelationship();
                neo4jEdges.add(neo4jEdge);
            }

            System.out.println("Number of nodes: " + nodes.size());
            System.out.println("Number of relationships: " + neo4jEdges.size());

            // Initialize database and capabilities
            DatabaseId databaseId = DatabaseId.EMPTY;
            DatabaseInfo.DatabaseLocation databaseLocation = DatabaseInfo.DatabaseLocation.LOCAL;
            DatabaseId remoteDatabaseId = DatabaseId.EMPTY;
            DatabaseInfo databaseInfo = ImmutableDatabaseInfo.of(databaseId, databaseLocation, Optional.empty());

            Capabilities capabilities = new CapabilitiesDTO() {
                @Override
                public WriteMode writeMode() {
                    return WriteMode.LOCAL;
                }
            };

            // Define graph schema
            MutableNodeSchema nodeSchema = MutableNodeSchema.empty().addLabel(NodeLabel.of("Person"));
            MutableRelationshipSchema relationshipSchema = MutableRelationshipSchema.empty().addRelationshipType(RelationshipType.of("KNOWS"), Direction.DIRECTED);
            Map<String, PropertySchema> graphProperties = Collections.emptyMap();
            MutableGraphSchema schema = MutableGraphSchema.of(nodeSchema, relationshipSchema, graphProperties);

            // Add nodes and properties
            IdMap gsIdMap = new DirectIdMap(nodes.size());
            for (Map.Entry<Long, Node> entry : nodes.entrySet()) {
                Iterable<String> labelsIterable = entry.getValue().labels();
                labelsIterable.forEach(label -> gsIdMap.addNodeIdToLabel(entry.getKey(), NodeLabel.of(label)));
            }

            Map<String, NodeProperty> propertyMap = new HashMap<>();
            NodePropertyStore gsProperties = ImmutableNodePropertyStore.of(propertyMap);
            Nodes gsNodes = ImmutableNodes.of(nodeSchema, gsIdMap, gsProperties);

            // Add relationships
            Map<RelationshipType, SingleTypeRelationships> importResults = new HashMap<>();
            AdjacencyList adjacencyList = AdjacencyList.EMPTY;
            long elementCount = neo4jEdges.size();
            Topology topology = ImmutableTopology.of(adjacencyList, elementCount, false);

            MutableRelationshipSchemaEntry relationshipSchemaEntry = new MutableRelationshipSchemaEntry(RelationshipType.of("KNOWS"), Direction.DIRECTED);
            importResults.put(RelationshipType.of("KNOWS"), ImmutableSingleTypeRelationships.of(topology, relationshipSchemaEntry, Optional.empty(), Optional.empty(), Optional.empty()));
            RelationshipImportResult relationshipImportResult = ImmutableRelationshipImportResult.of(importResults);

            // Define graph property store
            Optional<GraphPropertyStore> graphP = Optional.empty();
            int concurrency = 1;

            // Create graph store
            GraphStore gs = CSRGraphStore.of(databaseInfo, capabilities, schema, gsNodes, relationshipImportResult, graphP, concurrency);

            // Retrieve graph
            Graph g = ConnectConfig.graphStore.getGraph(NodeLabel.listOf("Person"), RelationshipType.listOf("KNOWS"), Optional.empty());

            // Define ID function mapping
            IdFunction mappedId = name -> ConnectConfig.graph.toMappedNodeId(ConnectConfig.idFunction.of(name));
        } finally {
            ConnectConfig.driver.close();
        }
    }

    private static void printNodeInfo(Node node) {
        System.out.println("Node ID: " + node.id());
        System.out.println("Labels: " + node.labels());
        System.out.println("Properties: " + node.asMap());
    }
}
