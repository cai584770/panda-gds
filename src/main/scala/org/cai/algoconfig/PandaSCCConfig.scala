package org.cai.algoconfig

import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.huge.HugeGraph
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.scc.Scc

/**
 * @author cai584770
 * @date 2024/7/10 13:02
 * @Version
 */
object PandaSCCConfig {

  def scc(hugeGraph: HugeGraph,
          progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
         ): HugeLongArray = {
    new Scc(hugeGraph, progressTracker).compute()
  }

}
