package org.cai.plugins

import org.cai.AlgoFunc.PathSearchFunctions
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 18:45
 * @Version
 */
class PathSearchPlugin extends ExtensionTypePlugin {
  override def registerAll(): Unit = {
    registerFunction(classOf[PathSearchFunctions])
  }

  override def getName: String = "PathSearchPluginPlugin"
}