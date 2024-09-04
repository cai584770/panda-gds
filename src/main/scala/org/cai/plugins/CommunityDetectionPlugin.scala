package org.cai.plugins

import org.cai.algofunc.CommunityDetectionFunctions
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 18:46
 * @Version
 */
class CommunityDetectionPlugin extends ExtensionTypePlugin {
  override def registerAll(): Unit = {
    registerFunction(classOf[CommunityDetectionFunctions])
  }

  override def getName: String = "CommunityDetectionPlugin"
}