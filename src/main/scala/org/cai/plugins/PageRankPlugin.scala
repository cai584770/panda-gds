package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.{DFSFunction, PageRankFunction}
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:10
 * @Version
 */
class PageRankPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[PageRankFunction])
  }

  override def getName: String = "PageRank"
}
