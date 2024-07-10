package org.cai.pandadb.algoconfig

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

  def scc(hugeGraph: HugeGraph): HugeLongArray = {
    val scc: Scc = new Scc(hugeGraph, ProgressTracker.NULL_TRACKER)
    val result: HugeLongArray = scc.compute()

    result
  }

}
