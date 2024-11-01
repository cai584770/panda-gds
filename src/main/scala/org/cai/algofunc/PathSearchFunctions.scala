package org.cai.algofunc

import org.cai.algoconfig.path.{PandaBFSConfig, PandaDFSConfig, PandaDijkstraSingleSourceShortestConfig, PandaDijkstraSourceTargetShortestConfig}
import org.cai.graph.GraphConversion
import org.grapheco.lynx.LynxRecord
import org.grapheco.lynx.func.{LynxProcedure, LynxProcedureArgument}
import org.grapheco.lynx.types.LynxValue
import org.grapheco.lynx.types.composite.LynxList
import org.grapheco.lynx.types.property.{LynxInteger, LynxString}
import org.grapheco.lynx.types.structural.{LynxNode, LynxPath}
import org.grapheco.pandadb.PandaInstanceContext
import org.grapheco.pandadb.facade.{GraphFacade, PandaTransaction}
import org.grapheco.pandadb.plugin.typesystem.TypeFunctions
import org.neo4j.gds.RelationshipType
import org.neo4j.gds.paths.PathResult

import java.util
import scala.collection.immutable
import scala.collection.mutable.ListBuffer


/**
 * @author cai584770
 * @date 2024/7/25 18:41
 * @Version
 */
class PathSearchFunctions extends TypeFunctions {

  private val embeddedDB: GraphFacade = PandaInstanceContext.getObject("embeddedDB").asInstanceOf[GraphFacade]

  @LynxProcedure(name = "BFS.compute")
  def computeBFS(
                  @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                  @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                  source: LynxInteger,
                  targets: LynxList
                ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList
    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))
    val target: List[Long] = targets.value.map(l => l.value.asInstanceOf[Long])

    val BFSResult: Array[Long] = PandaBFSConfig.BFS(hugeGraph, source.value,target)

    val count: Int = BFSResult.length
    val mapListBuffer = ListBuffer[LynxValue]()
    for (cursor <- 0 until count) {
      mapListBuffer += LynxValue(nodeRecords(cursor).values.toList)
    }

    val mapList: immutable.Seq[LynxValue] = mapListBuffer.toList
    LynxValue(mapList)

  }

  @LynxProcedure(name = "DFS.compute")
  def computeDFS(
                  @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
                  @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
                  source: LynxInteger,
                  targets: LynxList
                ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList
    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val target: List[Long] = targets.value.map(l => l.value.asInstanceOf[Long])
    val DFSResult: Array[Long] = PandaDFSConfig.DFS(hugeGraph, source.value, target)

    val count: Int = DFSResult.length
    val mapListBuffer = ListBuffer[LynxValue]()
    for (cursor <- 0 until count) {
      mapListBuffer += LynxValue(nodeRecords(cursor).values.toList)
    }

    val mapList: immutable.Seq[LynxValue] = mapListBuffer.toList
    LynxValue(mapList)
  }

  @LynxProcedure(name = "Dijkstra.sssp")
  def dijkstraSSSP(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          @LynxProcedureArgument(name = "sourceID") source: LynxInteger
        ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList
    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))
    val dijkstraSingleSourceShortestResult: util.Set[PathResult] = PandaDijkstraSingleSourceShortestConfig.dijkstraSingleSourceShortest(hugeGraph, source.value)

    val resultListBuffer = ListBuffer[LynxValue]()
    dijkstraSingleSourceShortestResult.forEach { s =>
      val ids = s.nodeIds()
      val count = s.nodeIds().length
      val pathResultListBuffer = ListBuffer[LynxValue]()
      for (cursor <- 0 until count) {
        val id: Long = ids(cursor)
        pathResultListBuffer += LynxValue(nodeRecords(id.toInt).values.toList)
      }
      resultListBuffer += LynxValue(pathResultListBuffer.toList)
    }

    LynxValue(resultListBuffer.toList)
  }

  @LynxProcedure(name = "Dijkstra.stsp")
  def dijkstraSTSP(
          @LynxProcedureArgument(name = "nodeLabel") nodeLabel: LynxString,
          @LynxProcedureArgument(name = "relationshipLabel") relationshipLabel: LynxString,
          source: LynxInteger,
          target: LynxInteger
        ): LynxValue = {
    val nodesQuery: String = s"match (n:${nodeLabel}) return n;"
    val relationshipsQuery: String = s"MATCH (n:${nodeLabel})-[r:${relationshipLabel}]->(m:${nodeLabel}) RETURN r;"
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val nodeRecords = tx.executeQuery(nodesQuery).records().toList
    val relationshipsRecords = tx.executeQuery(relationshipsQuery).records().toList
    val hugeGraph = GraphConversion.convertWithId(nodeRecords, relationshipsRecords, RelationshipType.of(relationshipLabel.value))

    val dijkstraSourceTargetShortestResult: util.Set[PathResult] = PandaDijkstraSourceTargetShortestConfig.dijkstraSourceTargetShortest(hugeGraph, source.value, target.value)

    val resultListBuffer = ListBuffer[LynxValue]()
    dijkstraSourceTargetShortestResult.forEach { s =>
      val ids = s.nodeIds()
      val count = s.nodeIds().length
      val pathResultListBuffer = ListBuffer[LynxValue]()
      for (cursor <- 0 until count) {
        val id: Long = ids(cursor)
        pathResultListBuffer += LynxValue(nodeRecords(id.toInt).values.toList)
      }
      resultListBuffer += LynxValue(pathResultListBuffer.toList)
    }
    LynxValue(resultListBuffer.toList)
  }

  @LynxProcedure(name = "path.shortest")
  def shortestPath(
          @LynxProcedureArgument(name = "sLimit") s: LynxInteger,
          @LynxProcedureArgument(name = "eLimit") e: LynxInteger,
          @LynxProcedureArgument(name = "sourceNodeID") source: LynxInteger,
          @LynxProcedureArgument(name = "targetNodeID") target: LynxInteger
        ): LynxValue = {
    val tx: PandaTransaction = embeddedDB.beginTransaction()

    val query: String =
      s"""
         |match p = (m)
         |-[$s..$e]-(n) where id(m)=$source and id(n)=$target return p
         |""".stripMargin
    val nodeRecords: Iterator[LynxRecord] = tx.executeQuery(query).records()
    val r: List[LynxPath] = nodeRecords.map(x => x.values).map(x => x.asInstanceOf[LynxPath]).toList
    val n: List[List[LynxNode]] = r.map(x => x.nodes)
    val shortestList: List[LynxNode] = n.minBy(_.length)

    LynxValue(shortestList)
  }


}

