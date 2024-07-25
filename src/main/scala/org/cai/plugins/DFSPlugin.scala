package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.DFSFunction
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:07
 * @Version
 */
class DFSPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[DFSFunction])
  }

  override def getName: String = "DFS"
}
