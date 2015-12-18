package net.iceyang.service

import reactivemongo.bson._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{ Failure, Success }

import net.iceyang.model._
import net.iceyang.model.Implicits._

class VipcCommunityNginxLogService(val dbCollection: BSONCollection) extends NginxLogService {
  val defaultPath = "/data/nginx_logs/o.vipc.access/"
  def dealLog(nginxLog: NginxLog) {
    save(nginxLog)
  }

  def save(nginxLog: NginxLog) {
    //println(nginxLog)
    val doc:BSONDocument = NginxLogHandler.write(nginxLog)

    dbCollection.insert(doc).onComplete {
      case Failure(e) => {
        println(s"$nginxLog")
        throw e
      }
      case Success(writeResult) =>
        //println(s"successfully inserted document with result: $writeResult")
    }
  }
}
