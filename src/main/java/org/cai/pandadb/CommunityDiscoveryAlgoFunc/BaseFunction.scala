package org.cai.pandadb.CommunityDiscoveryAlgoFunc

import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.GraphFacade

/**
 * @author cai584770
 * @date 2024/7/20 10:25
 * @Version
 */
object BaseFunction {
  val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

}
