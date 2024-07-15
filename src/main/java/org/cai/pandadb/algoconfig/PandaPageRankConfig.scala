package org.cai.pandadb.algoconfig

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

  def defaultPageRank(hugeGraph: HugeGraph): LongToDoubleFunction = {
    val build: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(40).concurrency(1).tolerance(0).build

    new PageRankAlgorithmFactory(Mode.PAGE_RANK).build(hugeGraph, build, ProgressTracker.NULL_TRACKER).compute().centralityScoreProvider()

  }

  def pageRank(hugeGraph: HugeGraph, maxIterations: Int, concurrency: Int, tolerance: Int, mode: Mode, progressTracker: ProgressTracker): LongToDoubleFunction = {
    val build: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(maxIterations).concurrency(concurrency).tolerance(tolerance).build

    new PageRankAlgorithmFactory(mode).build(hugeGraph, build, progressTracker).compute().centralityScoreProvider()

  }


}
