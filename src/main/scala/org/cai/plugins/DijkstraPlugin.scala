package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.{DFSFunction, DijkstraFunctions}
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:09
 * @Version
 */
class DijkstraPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[DijkstraFunctions])
  }

  override def getName: String = "Dijkstra"
}