package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.{BFSFunction, DFSFunction}
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:09
 * @Version
 */
class BFSPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[BFSFunction])
  }

  override def getName: String = "BFS"
}
