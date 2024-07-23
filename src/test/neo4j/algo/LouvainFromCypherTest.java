package neo4j.algo;

import org.neo4j.driver.*;

/**
 * @author cai584770
 * @date 2024/7/1 12:44
 * @Version
 */
public class LouvainFromCypherTest {

    public static void main(String[] args) {

        try (Session session = ConnectConfig.driver.session()) {

            session.writeTransaction(new TransactionWork<Void>() {
                @Override
                public Void execute(Transaction tx) {
                    Result checkResult = tx.run("CALL gds.graph.exists('personGraph') YIELD exists RETURN exists");
                    if (checkResult.hasNext() && checkResult.next().get("exists").asBoolean()) {
                        tx.run("CALL gds.graph.drop('personGraph')");
                    }

                    tx.run("CALL gds.graph.project(" +
                            "'personGraph', " +
                            "'Person', " +
                            "'KNOWS'" +
                            ") YIELD graphName, nodeCount, relationshipCount");

                    tx.run("CALL gds.louvain.write(" +
                            "'personGraph', " +
                            "{ writeProperty: 'community' }" +
                            ") YIELD communityCount, modularity");

                    return null;
                }
            });

            Result result = session.run(
                    "MATCH (p:Person) " +
                            "RETURN p.id AS id, p.community AS community " +
                            "ORDER BY p.community " +
                            "LIMIT 10"
            );

            while (result.hasNext()) {
                Record record = result.next();
                long personId = record.get("id").asLong();
                long community = record.get("community").asLong();
                System.out.println("Person ID: " + personId + ", Community: " + community);
            }

            session.run("CALL gds.graph.drop('personGraph');");

        } finally {
            ConnectConfig.driver.close();
        }
    }


}
