package net.iceyang.service

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global

class MongoService(connection: MongoConnection) {
  def db(name: String): DefaultDB = connection(name)
}

object MongoService {
  def apply(connection: MongoConnection) = new MongoService(connection)
  def apply(host: String, port: Int): MongoService = {
    val driver = new MongoDriver
    val connection = driver.connection(List(s"$host:$port"))
    MongoService(connection)
  }
}
