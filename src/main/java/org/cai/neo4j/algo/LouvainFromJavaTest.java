package org.cai.neo4j.algo;


import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.neo4j.gds.NodeLabel;
import org.neo4j.gds.Orientation;
import org.neo4j.gds.RelationshipType;
import org.neo4j.gds.collections.ha.HugeLongArray;
import org.neo4j.gds.core.concurrency.DefaultPool;
import org.neo4j.gds.core.huge.HugeGraph;
import org.neo4j.gds.core.loading.Nodes;
import org.neo4j.gds.core.loading.SingleTypeRelationships;
import org.neo4j.gds.core.loading.construction.GraphFactory;
import org.neo4j.gds.core.loading.construction.NodesBuilder;
import org.neo4j.gds.core.loading.construction.RelationshipsBuilder;
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker;
import org.neo4j.gds.louvain.Louvain;
import org.neo4j.gds.louvain.LouvainResult;
import org.neo4j.gds.louvain.LouvainStreamConfig;
import org.neo4j.gds.louvain.LouvainStreamConfigImpl;
import org.neo4j.gds.termination.TerminationFlag;
import org.neo4j.values.storable.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT;

/**
 * @author cai584770
 * @date 2024/7/1 13:02
 * @Version
 */
public class LouvainFromJavaTest {

    public static void main(String[] args) {
        try (Session session = ConnectConfig.driver.session()) {

            List<Relationship> neo4jEdges = new ArrayList<>();
            Map<Long, Node> nodes = new HashMap<>();

            Result nodeResult = session.run("MATCH (n:Person) RETURN n");
            long maxNodeId = 0L;
            while (nodeResult.hasNext()) {
                Record record = nodeResult.next();
                Node neo4jNode = record.get("n").asNode();
                if (maxNodeId <= neo4jNode.id()) {
                    maxNodeId = neo4jNode.id();
                }
                nodes.put(neo4jNode.id(), neo4jNode);
//                printNodeInfo(neo4jNode);
            }

            Result edgeResult = session.run("MATCH (n:Person)-[r:KNOWS]->(m:Person) RETURN r");
            while (edgeResult.hasNext()) {
                Record record = edgeResult.next();
                Relationship neo4jEdge = record.get("r").asRelationship();
                neo4jEdges.add(neo4jEdge);
            }

            System.out.println("Number of nodes: " + nodes.size());
            System.out.println("Number of relationships: " + neo4jEdges.size());

            long nodesCount = nodes.size();
            long relationsCount = neo4jEdges.size();

//          ------------------------------------------------
            NodesBuilder nodesBuild1 = GraphFactory.initNodesBuilder()
                    .nodeCount(nodesCount)
                    .maxOriginalId(maxNodeId)
                    .hasLabelInformation(false)
                    .build();

            for (Map.Entry<Long, Node> entry : nodes.entrySet()) {
                Long id = entry.getKey();
                Node node = entry.getValue();
                Iterable<String> labels = node.labels();
                String[] nodeLabels = StreamSupport.stream(labels.spliterator(), false).toArray(String[]::new);

                Map<String, Object> properties = node.asMap();
                Map<String, org.neo4j.values.storable.Value> nodeProperties = new HashMap<>();
                for (Map.Entry<String, Object> e : properties.entrySet()) {
                    String key = e.getKey();
                    org.neo4j.values.storable.Value value = Values.of(e.getValue());
                }
//                System.out.println(id);
                nodesBuild1.addNode(id, nodeProperties, NodeLabel.of("Person"));
            }

            Nodes build1 = nodesBuild1.build();

            RelationshipsBuilder rel = GraphFactory.initRelationshipsBuilder()
                    .nodes(build1.idMap())
                    .relationshipType(RelationshipType.of("KNOWS"))
                    .orientation(Orientation.NATURAL)
                    .build();

            for (Relationship rs : neo4jEdges) {
                long startNodeId = rs.startNodeId();
                long endNodeId = rs.endNodeId();
                rel.add(startNodeId, endNodeId);
            }

            SingleTypeRelationships buildrel = rel.build();

            HugeGraph hugeGraph = GraphFactory.create(build1.idMap(), buildrel);

            System.out.println(hugeGraph.nodeCount());
            System.out.println(hugeGraph.relationshipCount());

//          -------------------louvain----------------
            LouvainStreamConfig config = LouvainStreamConfigImpl.builder()
                    .maxLevels(1000)
                    .maxIterations(10)
                    .tolerance(TOLERANCE_DEFAULT)
                    .includeIntermediateCommunities(true)
                    .concurrency(1).build();

            Louvain louvain = new Louvain(hugeGraph,
                    config,
                    config.includeIntermediateCommunities(),
                    config.maxLevels(),
                    config.maxIterations(),
                    config.tolerance(),
                    config.concurrency(),
                    ProgressTracker.NULL_TRACKER,
                    DefaultPool.INSTANCE);

            louvain.setTerminationFlag(TerminationFlag.RUNNING_TRUE);

            LouvainResult compute = louvain.compute();

            HugeLongArray[] dendrogram = compute.dendrogramManager().getAllDendrograms(); //代表节点的社区分配情况
            double[] modularities = compute.modularities(); //相应层级的模块度

            for (int i = 0; i < dendrogram.length; i++) {
                System.out.println("dendrogram[" + i + "]:" + dendrogram[i]);
                System.out.println("modularities[" + i + "]:" + modularities[i]);
            }


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
