package net.iceyang

import reactivemongo.bson._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import com.github.nscala_time.time.Imports._

import net.iceyang.service._
import net.iceyang.model._

object Main { 
  DateTimeZone.setDefault(DateTimeZone.UTC)

  def main(args: Array[String]):Unit = {
    ngtest()
  }

  def ngtest() {
    val mongoService = MongoService("localhost", 27017)
    //val db = mongoService.db("o-vipc-access")
    //val filename = "2015-11-15"
    val db = mongoService.db("vipc-access")
    val filename = "2015-11-01"
    val coll = db[BSONCollection](filename)
    //val logService:NginxLogService = new VipcCommunityNginxLogService(coll)
    val logService:NginxLogService = new VipcNginxLogService(coll)
    logService.dealLog(filename)
  }

}
