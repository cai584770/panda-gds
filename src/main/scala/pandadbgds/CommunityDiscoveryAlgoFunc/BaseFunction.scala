package pandadbgds.CommunityDiscoveryAlgoFunc

import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.types.property.LynxString
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}

import scala.collection.immutable

/**
 * @author cai584770
 * @date 2024/7/20 10:25
 * @Version
 */
object BaseFunction {
  val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

  def query(nodeLabel: LynxString, relationshipLabel: LynxString): (immutable.Seq[LynxRecord], immutable.Seq[LynxRecord]) = {

    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = BaseFunction.embeddedDB.beginTransaction()

    tx.commit()
    tx.close()

    (tx.executeQuery(nodesQuery).records().toList, tx.executeQuery(relationshipsQuery).records().toList)

  }



}
