package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.{DFSFunction, LouvainFunction}
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:10
 * @Version
 */
class LouvainPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[LouvainFunction])
  }

  override def getName: String = "Louvain"
}
