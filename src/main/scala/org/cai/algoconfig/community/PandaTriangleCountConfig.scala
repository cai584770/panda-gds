package org.cai.algoconfig.community

import org.neo4j.gds.collections.haa.HugeAtomicLongArray
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.triangle.{IntersectingTriangleCount, TriangleCountBaseConfigImpl, TriangleCountResult}

import java.util.concurrent.ExecutorService
import java.util.{Map => JMap}

/**
 * @author cai584770
 * @date 2024/9/4 15:49
 * @Version
 */
object PandaTriangleCountConfig {

  def triangleCount(hugeGraph: HugeGraph,
                    javaMap: JMap[String, Object],
                    progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
                    executorService: ExecutorService = DefaultPool.INSTANCE):Long={

    val wrapper: CypherMapWrapper = CypherMapWrapper.create(javaMap)
    val triangleCountConfig: TriangleCountBaseConfigImpl = new TriangleCountBaseConfigImpl(wrapper)

    val compute: TriangleCountResult = IntersectingTriangleCount.create(hugeGraph, triangleCountConfig, executorService, progressTracker).compute

    compute.localTriangles().size()
  }


}
