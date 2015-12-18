package net.iceyang.model

import reactivemongo.bson.{ BSON, BSONHandler, BSONDateTime, Macros, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONString }
import com.github.nscala_time.time.Imports._
import org.joda.time.format.ISODateTimeFormat

object Implicits {
  implicit object BSONDateTimeHandler extends BSONHandler[BSONDateTime, DateTime] {
    val fmt = ISODateTimeFormat.dateTime()
    def write(jdtime: DateTime) = BSONDateTime(jdtime.getMillis)
    def read(time: BSONDateTime) = new DateTime(time.value)
  }

  implicit object MapHandler extends BSONHandler[BSONDocument, Map[String, String]] {
    def write(map: Map[String, String]): BSONDocument = {
      val elements = map.toStream.map { tuple => 
        tuple._1 -> BSONString(tuple._2)
      }
      BSONDocument(elements)
    }
    def read(doc: BSONDocument): Map[String, String] = {
      val elements = doc.elements.map { tuple =>
        tuple._1 -> tuple._2.seeAsTry[String].get
      }
      elements.toMap
    }
  }

  implicit def NginxLogHandler: BSONHandler[BSONDocument, NginxLog] = Macros.handler[NginxLog]
}
