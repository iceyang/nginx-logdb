package net.iceyang.service

import java.io.File
import scala.io.Source
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection

import net.iceyang.model._
import net.iceyang.utils._

trait NginxLogService {
  val defaultPath:String
  final val EXCLUDE_URL = """^\/(feed|404|sitemap)(\/|$)""".r
  final val EXCLUDE_APP = """\.(apk)$""".r
  final val STATIC_FILE = """\.(css|js|png|jpg|ico|txt|xml)$""".r
  final val SPIDERS = Map(
    "google" -> List(
      "http://www.google.com/bot.html",
      "http://www.googlebot.com/bot.htm"
    ),
    "baidu" -> List(
      "http://www.baidu.com/search/spider.html"
    ),
    "yahoo" -> List(
      "http://misc.yahoo.com.cn/help.html",
      "http://help.yahoo.com/help/us/ysearch/slurp"
    ),
    "iask" -> List(
      "http://iask.com/help/help_index.html",
      "iaskspider/"
    ),
    "sogou" -> List(
      "http://www.sogou.com/docs/help/webmasters.htm"
    ),
    "yodao" -> List(
      "http://www.yodao.com/help/webmaster/spider"
    ),
    "msn" -> List(
      "http://search.msn.com/msnbot.htm"
    ),
    "bing" -> List(
      "http://www.bing.com/bingbot.htm"
    )
  )

  def dealLog(filename: String) { 
    val file = new File(s"${defaultPath}${filename}")
    dealLog(file) 
  }

  def dealLog(file: File) {
    if (file.isDirectory) {
      file.listFiles.foreach(dealLog)
    } else {
      Source.fromFile(file).getLines().foreach((log)=>{
        val nginxLog = NginxLogParser.parse(log)
        if (!isExcludeLog(nginxLog))
          dealLog(nginxLog)
      })
    }
  }

  def dealLog(nginxLog: NginxLog)

  def isStatisFileLog(nginxLog: NginxLog) = STATIC_FILE.findAllIn(nginxLog.baseUrl).toArray.length > 0

  def isSpiderLog(nginxLog: NginxLog) = (SPIDERS count {
    case (key:String, value:List[String]) => (value count {nginxLog.userAgent.contains(_)}) > 0
  }) > 0
  
  def isExcludeLog(nginxLog: NginxLog): Boolean = 
    EXCLUDE_URL.findAllIn(nginxLog.baseUrl).toArray.length > 0 || 
    EXCLUDE_APP.findAllIn(nginxLog.baseUrl).toArray.length > 0 ||
    isStatisFileLog(nginxLog) ||
    isSpiderLog(nginxLog)
}
