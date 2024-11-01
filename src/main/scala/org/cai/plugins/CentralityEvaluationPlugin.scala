package org.cai.plugins

import org.cai.algofunc.CentralityEvaluationFunctions
import org.grapheco.pandadb.plugin.typesystem.ExtensionTypePlugin

/**
 * @author cai584770
 * @date 2024/7/25 18:46
 * @Version
 */
class CentralityEvaluationPlugin extends ExtensionTypePlugin {
  override def registerAll(): Unit = {
    registerFunction(classOf[CentralityEvaluationFunctions])
  }

  override def getName: String = "CentralityEvaluationPlugin"
}