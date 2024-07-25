package org.cai.plugins

import org.cai.CommunityDiscoveryAlgoFunc.{DFSFunction, LabelPropagationFunction}
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 9:10
 * @Version
 */
class LabelPropagationPlugin extends ExtensionTypePlugin{
  override def registerAll(): Unit = {
    registerFunction(classOf[LabelPropagationFunction])
  }

  override def getName: String = "LabelPropagation"
}
