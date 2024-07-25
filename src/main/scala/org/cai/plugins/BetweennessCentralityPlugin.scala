package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.BetweennessCentralityFunction
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:09
 * @Version
 */
class BetweennessCentralityPlugin extends ExtensionTypePlugin {
  override def registerAll(): Unit = {
    registerFunction(classOf[BetweennessCentralityFunction])
  }

  override def getName: String = "BetweennessCentrality"
}
