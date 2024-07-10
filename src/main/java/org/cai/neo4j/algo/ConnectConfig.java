package org.cai.neo4j.algo;

import org.neo4j.driver.*;
import org.neo4j.gds.api.Graph;
import org.neo4j.gds.api.GraphStore;

/**
 * @author cai584770
 * @date 2024/7/1 12:45
 * @Version
 */
public class ConnectConfig {
    static String uri = "bolt://localhost:7687";
    static String user = "neo4j";
    static String password = "cai584770";

    static Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));

    static GraphStore graphStore;
    static Graph graph;
    static IdFunction idFunction;
}
