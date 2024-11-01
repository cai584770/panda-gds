package org.cai.algoconfig.community

import org.neo4j.gds.collections.haa.HugeAtomicLongArray
import org.neo4j.gds.core.CypherMapWrapper
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.triangle.{IntersectingTriangleCount, TriangleCountBaseConfigImpl, TriangleCountResult, TriangleStream}

import java.util.concurrent.ExecutorService
import java.util.{Map => JMap}
import scala.collection.mutable.ListBuffer

/**
 * @author cai584770
 * @date 2024/9/4 15:49
 * @Version
 */
object PandaTriangleCountConfig {

  def triangleCountStats(hugeGraph: HugeGraph,
                    javaMap: JMap[String, Object] = null,
                    progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER,
                    executorService: ExecutorService = DefaultPool.INSTANCE):(Long, HugeAtomicLongArray)={

    val wrapper: CypherMapWrapper = CypherMapWrapper.create(javaMap)
    val triangleCountConfig: TriangleCountBaseConfigImpl = new TriangleCountBaseConfigImpl(wrapper)

    val computeResult: TriangleCountResult = IntersectingTriangleCount.create(hugeGraph, triangleCountConfig, executorService, progressTracker).compute

    (computeResult.globalTriangles(),computeResult.localTriangles())
  }

  def triangleCountStream(hugeGraph: HugeGraph,
                         executorService: ExecutorService = DefaultPool.INSTANCE,
                          concurrency:Int = 4
                         ): List[List[Long]] = {

    val streamResults = TriangleStream.create(hugeGraph, executorService, concurrency).compute().toArray()

    val resultList = ListBuffer[List[Long]]()

    for (result <- streamResults) {
      result match {
        case re: TriangleStream.Result =>
          resultList += List(re.nodeA, re.nodeB, re.nodeC)
        case _ =>
          println("Unknown type")
      }
    }

    resultList.toList
  }

}
