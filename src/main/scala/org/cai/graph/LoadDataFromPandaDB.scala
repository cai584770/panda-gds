package org.cai.graph

import org.grapheco.lynx.{LynxRecord, LynxResult}
import org.grapheco.pandadb.GraphDataBaseBuilder
import org.grapheco.pandadb.facade.{GraphDatabaseService, PandaTransaction}

import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/7/9 15:25
 * @Version
 */
object LoadDataFromPandaDB {

  def getNodeAndRelationship(dbPath: String, nodeLabel: String, relationshipLabel: String): (immutable.Seq[LynxRecord],immutable.Seq[LynxRecord]) = {
    val nodesQuery = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"

    val db: GraphDatabaseService = GraphDataBaseBuilder.newEmbeddedDatabase(dbPath)
    val tx: PandaTransaction = db.beginTransaction()

    val nodeResult: LynxResult = tx.executeQuery(nodesQuery)
    val relationshipResult: LynxResult = tx.executeQuery(relationshipsQuery)

    val nodesRecordsList: immutable.Seq[LynxRecord] = nodeResult.records().toList
    val relationshipsRecordList: immutable.Seq[LynxRecord] = relationshipResult.records().toList

    tx.commit()
    tx.close()
    db.close()

    (nodesRecordsList,relationshipsRecordList)
  }

}
