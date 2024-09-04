package panda.convert

import org.cai.algoconfig.community.PandaLouvainConfig
import org.cai.algoconfig.path.{PandaBFSConfig, PandaDFSConfig, PandaDijkstraSingleSourceShortestConfig, PandaDijkstraSourceTargetShortestConfig}
import org.cai.graph.{GraphConversion, LoadDataFromPandaDB}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.LynxInteger
import org.junit.jupiter.api.Test
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.collections.ha.HugeLongArray
import org.neo4j.gds.core.ProcedureConstants.TOLERANCE_DEFAULT
import org.neo4j.gds.core.concurrency.DefaultPool
import org.neo4j.gds.core.utils.progress.tasks.ProgressTracker
import org.neo4j.gds.pagerank.{PageRankAlgorithmFactory, PageRankStreamConfig, PageRankStreamConfigImpl}
import org.neo4j.gds.pagerank.PageRankAlgorithmFactory.Mode
import org.neo4j.gds.paths.PathResult
import org.neo4j.gds.paths.dijkstra.Dijkstra
import org.neo4j.gds.paths.dijkstra.config.{AllShortestPathsDijkstraStreamConfig, AllShortestPathsDijkstraStreamConfigImpl}
import org.neo4j.gds.paths.traverse.{Aggregator, DfsBaseConfig, ExitPredicate, TargetExitPredicate}
import org.neo4j.gds.termination.TerminationFlag

import java.util
import java.util.Optional
import scala.collection.JavaConverters.seqAsJavaListConverter


/**
 * @author cai584770
 * @date 2024/7/25 16:31
 * @Version
 */
class LynxValueTest {

  @Test
  def test01(): Unit = {
    val initializedArray: Array[Double] = Array(1.0, 2.0, 3.0, 4.0, 5.0)

    val value = LynxValue.apply(initializedArray)

    println(value)

  }

  @Test
  def test02(): Unit = {
    val dbPath = "/home/cjw/lsbc.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Person", "KNOWS")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("KNOWS"))

    val (dendrogram, modularities): (Array[HugeLongArray], Array[Double]) = PandaLouvainConfig.louvain(hg, TOLERANCE_DEFAULT, 10, includeIntermediateCommunities = true, 1, ProgressTracker.NULL_TRACKER, DefaultPool.INSTANCE, TerminationFlag.RUNNING_TRUE)

    val value = LynxValue.apply(modularities)

    println(value)

    for (den: HugeLongArray <- dendrogram) {
      val d: Array[Long] = den.toArray()
      val v: LynxValue = LynxValue.apply(d)
      println(v)
    }

    println("-------------")


  }


  @Test
  def pr2LynxValue(): Unit = {
    val dbPath = "/home/cjw/ppr.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("TYPE"))

    val build: PageRankStreamConfig = PageRankStreamConfigImpl.builder.maxIterations(40).concurrency(1).tolerance(0).build

    val pageRankResult = new PageRankAlgorithmFactory(Mode.PAGE_RANK).build(hg, build, ProgressTracker.NULL_TRACKER).compute().centralityScoreProvider()

    var result: Array[Double] = Array.empty[Double]

    val count: Int = hg.idMap().nodeCount().toInt
    for (cursor <- 0 until count) {
      result = result :+ pageRankResult.applyAsDouble(cursor.toLong)
      println(pageRankResult.applyAsDouble(cursor.toLong))
    }

    val value = LynxValue.apply(result)

    println(value)


  }

  @Test
  def bfs2LynxValue(): Unit = {
    val dbPath = "/home/cjw/bfs.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("REL"))

    val source: LynxInteger = LynxInteger(hg.toMappedNodeId(1L))

    val targets: LynxList = LynxList(List(
      LynxValue(3L)
    ))

    val target: List[LynxValue] = targets.value

    val javaLongList: List[java.lang.Long] = target.collect {
      case lynxValue if lynxValue.value.isInstanceOf[Long] =>
        java.lang.Long.valueOf(lynxValue.value.asInstanceOf[Long])
    }

    val exitPredicate: ExitPredicate = new TargetExitPredicate(javaLongList.asJava)

    val aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION
    val concurrency: Int = 1
    val maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER


    val BFSResult: Array[Long] = PandaBFSConfig.BFS(hg, source.value,
      exitPredicate,
      aggregatorFunction,
      concurrency,
      progressTracker,
      maxDepth)

    val lynxResult: LynxValue = LynxValue.apply(BFSResult)

    println(lynxResult)


  }

  @Test
  def dfs2LynxValue(): Unit = {
    val dbPath = "/home/cjw/bfs.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "REL")

    val hg = GraphConversion.convertWithId(nodeResult, relationshipResult, RelationshipType.of("REL"))

    val source: LynxInteger = LynxInteger(hg.toMappedNodeId(1L))

    val targets: LynxList = LynxList(List(
      LynxValue(3L)
    ))

    val target: List[LynxValue] = targets.value

    val javaLongList: List[java.lang.Long] = target.collect {
      case lynxValue if lynxValue.value.isInstanceOf[Long] =>
        java.lang.Long.valueOf(lynxValue.value.asInstanceOf[Long])
    }

    val exitPredicate: ExitPredicate = new TargetExitPredicate(javaLongList.asJava)

    val aggregatorFunction: Aggregator = Aggregator.NO_AGGREGATION
    val concurrency: Int = 1
    val maxDepth: Long = DfsBaseConfig.NO_MAX_DEPTH
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER

    val DFSResult: Array[Long] = PandaDFSConfig.DFS(hg, source.value, exitPredicate, aggregatorFunction, maxDepth, progressTracker)


    val lynxResult: LynxValue = LynxValue.apply(DFSResult)

    println(lynxResult)

  }

  @Test
  def ss2LynxValue(): Unit = {
    val dbPath = "/home/cjw/dijkstra.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("TYPE"))

    val concurrency: Int = 1
    val trackRelationships: Boolean = false
    val heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction]
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
    val terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE

    val source: LynxInteger = LynxInteger(hg.toOriginalNodeId(1L))
    println(hg.toOriginalNodeId(1L))

    println(source.value)

    val dijkstraSingleSourceShortestResult: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hg, source.value, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    var idsResult: Array[Array[Long]] = Array.empty[Array[Long]]

    val iterator = dijkstraSingleSourceShortestResult.iterator
    while (iterator.hasNext) {
      val dijkstraResult = iterator.next()
      idsResult = idsResult :+ dijkstraResult.nodeIds()
    }

    val lynxResult: LynxValue = LynxValue.apply(idsResult)


    println(lynxResult)

  }

  @Test
  def st2LynxValue(): Unit = {
    val dbPath = "/home/cjw/dijkstra.db"
    val (nodeResult, relationshipResult) = LoadDataFromPandaDB.getNodeAndRelationship(dbPath, "Node", "TYPE")

    val hg = GraphConversion.convertWithNodeLabel(nodeResult, relationshipResult, RelationshipType.of("TYPE"))

    val concurrency: Int = 1
    val trackRelationships: Boolean = false
    val heuristicFunction: Optional[Dijkstra.HeuristicFunction] = Optional.empty[Dijkstra.HeuristicFunction]
    val progressTracker: ProgressTracker = ProgressTracker.NULL_TRACKER
    val terminationFlag: TerminationFlag = TerminationFlag.RUNNING_TRUE

    val source: LynxInteger = LynxInteger(hg.toOriginalNodeId(1L))

    val target: LynxInteger = LynxInteger(hg.toOriginalNodeId(5L))

    val dijkstraSourceTargetShortestResult: util.Set[PathResult] = PandaDijkstraSourceTargetShortestConfig.dijkstraSourceTargetShortest(hg, source.value, target.value, concurrency, trackRelationships, heuristicFunction, progressTracker, terminationFlag)

    var idsResult: Array[Array[Long]] = Array.empty[Array[Long]]

    val iterator = dijkstraSourceTargetShortestResult.iterator
    while (iterator.hasNext) {
      val dijkstraResult = iterator.next()
      idsResult = idsResult :+ dijkstraResult.nodeIds()
    }

    val result: LynxValue = LynxValue.apply(idsResult)

    println(result)

  }


}
