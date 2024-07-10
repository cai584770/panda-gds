package org.cai.neo4j.algo;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
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
import org.neo4j.gds.core.cypher.nodeproperties.UpdatableLongNodeProperty;
import org.neo4j.gds.core.io.file.csv.CapabilitiesDTO;
import org.neo4j.gds.core.loading.*;
import org.neo4j.gds.core.write.ImmutableNodeProperty;

import java.util.*;

/**
 * @author cai584770
 * @date 2024/7/4 14:39
 * @Version
 */
public class GDSGraphCreateTest {

    public static void main(String[] args) {

        try (Session session = ConnectConfig.driver.session()) {
            List<Relationship> neo4jEdges = new ArrayList<>();
            Map<Long, Node> nodes = new HashMap<>();

            Result nodeResult = session.run("MATCH (n:Person) RETURN n");
            while (nodeResult.hasNext()) {
                Record record = nodeResult.next();
                Node neo4jNode = record.get("n").asNode();
                nodes.put(neo4jNode.id(), neo4jNode);
            }

            Result edgeResult = session.run("MATCH (n:Person)-[r:KNOWS]->(m:Person) RETURN r");
            while (edgeResult.hasNext()) {
                Record record = edgeResult.next();
                Relationship neo4jEdge = record.get("r").asRelationship();
                neo4jEdges.add(neo4jEdge);
            }

            System.out.println("Number of nodes: " + nodes.size());
            System.out.println("Number of relationships: " + neo4jEdges.size());

//          -------------Graph-------------------

            // DB -> empty
            DatabaseId databaseId = DatabaseId.EMPTY;
            DatabaseInfo.DatabaseLocation databaseLocation = DatabaseInfo.DatabaseLocation.LOCAL;
            DatabaseId remoteDatabaseId = DatabaseId.EMPTY;
            DatabaseInfo databaseInfo = ImmutableDatabaseInfo.of(databaseId, databaseLocation, Optional.empty());

            // Capability -> local
            Capabilities capabilities = new CapabilitiesDTO() {
                @Override
                public WriteMode writeMode() {
                    return WriteMode.LOCAL;
                }
            };

            // graph schema
            MutableNodeSchema nodeSchema = MutableNodeSchema.empty().addLabel(NodeLabel.of("Person"));
            MutableRelationshipSchema relationshipSchema = MutableRelationshipSchema.empty().addRelationshipType(RelationshipType.of("KNOWS"), Direction.DIRECTED);
            Map<String, PropertySchema> graphProperties = Collections.emptyMap();
            MutableGraphSchema schema = MutableGraphSchema.of(nodeSchema, relationshipSchema, graphProperties);

//             nodes
            IdMap gsIdMap = new DirectIdMap(nodes.size());


            gsIdMap.addNodeIdToLabel(1L, NodeLabel.of("Person"));
            NodeProperty property = (NodeProperty) ImmutableNodeProperty.of("key", new UpdatableLongNodeProperty(1L, 1L));

            Map<String, NodeProperty> propertyMap = new HashMap<>();
            propertyMap.put("properties", property);
            NodePropertyStore gsProperties = ImmutableNodePropertyStore.of(propertyMap);
            Nodes gsNodes = ImmutableNodes.of(nodeSchema, gsIdMap, gsProperties);

            // relations
            Map<RelationshipType, SingleTypeRelationships> importResults = new HashMap<>();

            AdjacencyList adjacencyList = AdjacencyList.EMPTY;
            long elementCount = 1L;
            Topology topology = ImmutableTopology.of(adjacencyList, elementCount, false);

            MutableRelationshipSchemaEntry relationshipSchemaEntry = new MutableRelationshipSchemaEntry(RelationshipType.of("KNOWS"), Direction.DIRECTED);
            Optional<RelationshipPropertyStore> properties = Optional.empty();
            Optional<Topology> inverseTopology = Optional.empty();
            Optional<RelationshipPropertyStore> inverseProperties = Optional.empty();

            importResults.put(RelationshipType.of("KNOWS"), ImmutableSingleTypeRelationships.of(topology, relationshipSchemaEntry, Optional.empty(), Optional.empty(), Optional.empty()));
            RelationshipImportResult relationshipImportResult = ImmutableRelationshipImportResult.of(importResults);

            // graph propery
            Optional<GraphPropertyStore> graphP = Optional.empty();

            // concurrency
            int concurrency = 1;

            // create a graph
            GraphStore gs = CSRGraphStore.of(databaseInfo, capabilities, schema, gsNodes, relationshipImportResult, graphP, concurrency);


            Graph g = gs.getGraph(NodeLabel.listOf("Person"),
                    RelationshipType.listOf("KNOWS"),
                    Optional.empty());


            IdFunction mappedId = name -> g.toMappedNodeId(ConnectConfig.idFunction.of(name));
        } finally {
            ConnectConfig.driver.close();
        }
    }

}

