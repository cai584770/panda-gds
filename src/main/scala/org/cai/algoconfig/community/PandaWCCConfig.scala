package org.cai.algoconfig.community

import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.paged.dss.DisjointSetStruct
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.wcc.{WccAlgorithmFactory, WccBaseConfig, WccMutateConfigImpl}
import java.util.{Map => JMap}

/**
 * @author cai584770
 * @date 2024/9/4 15:48
 * @Version
 */
object PandaWCCConfig {

  def wcc(hugeGraph: HugeGraph,
          javaMap: JMap[String, Object],
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
         ):Array[Long]={

    val config: WccMutateConfigImpl = new WccMutateConfigImpl(CypherMapWrapper.create(javaMap))
    val result: DisjointSetStruct = new WccAlgorithmFactory[WccBaseConfig]().build(hugeGraph, config, progressTracker).compute

    val communityData: Array[Long] = new Array[Long](hugeGraph.nodeCount().toInt)
    hugeGraph.forEachNode { nodeId =>
      communityData(nodeId.toInt) = result.setIdOf(nodeId)
      true
    }

    communityData
  }

}
