package org.cai.graph

/**
 * @author cai584770
 * @date 2024/9/13 15:52
 * @Version
 */
class IdMap(originalIds: Array[Long], mappedIds: Array[Long]) {

  private val originalToMapped: Map[Long, Long] = (originalIds zip mappedIds).toMap
  private val mappedToOriginal: Map[Long, Long] = (mappedIds zip originalIds).toMap

  def getOriginalId: Seq[Long] = originalIds

  def getMappedId: Seq[Long] = mappedIds

  def getMappedId(originalId: Long): Option[Long] = {
    originalToMapped.get(originalId)
  }

  def getOriginalId(mappedId: Long): Option[Long] = {
    mappedToOriginal.get(mappedId)
  }




}
