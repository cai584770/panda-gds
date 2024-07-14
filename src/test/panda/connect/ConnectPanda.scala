package panda.connect

import org.grapheco.pandadb.driver.PandaDBDriver

/**
 * @author cai584770
 * @date 2024/7/8 10:16
 * @Version
 */
class ConnectPanda {
  val host = "10.0.82.148"
  val port = 7600
  val panda: PandaDBDriver = new PandaDBDriver(host, port)

}
