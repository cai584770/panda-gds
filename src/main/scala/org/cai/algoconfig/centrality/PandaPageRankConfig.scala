package org.cai.algoconfig.centrality

import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode
import org.neo4j.gds.pagerank.{PageRankAlgorithmFactory, PageRankStreamConfig, PageRankStreamConfigImpl}

import java.util.function.LongToDoubleFunction

/**
 * @author cai584770
 * @date 2024/7/15 9:14
 * @Version
 */
object PandaPageRankConfig {

  def pageRank(
                hugeGraph: HugeGraph,
                maxIterations: Int = 40,
                concurrency: Int = 1,
                tolerance: Int = 0,
                mode: Mode = Mode.PAGE_RANK,
                progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
              ): LongToDoubleFunction = {

    val config: PageRankStreamConfig = PageRankStreamConfigImpl.builder
      .maxIterations(maxIterations)
      .concurrency(concurrency)
      .tolerance(tolerance)
      .build

    new PageRankAlgorithmFactory(mode).build(hugeGraph, config, progressTracker).compute().centralityScoreProvider()
  }

}
