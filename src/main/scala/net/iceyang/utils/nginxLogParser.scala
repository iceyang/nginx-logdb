package net.iceyang.utils

import net.iceyang.model._

import com.github.nscala_time.time.Imports._
import java.text.SimpleDateFormat
import java.util.Locale

object NginxLogParser {
  val PARSER_GLOBAL_REGEX = """([^\"\[ ][^ ]*|\"[^\"]*\"|\[[^\]]*\])""".r
  val PARSER_REQUEST_REGEX = """\"([^ ]+) ([^ \?]+)\??([^ ]*) (.*)\"""".r
  val REMOVE_SLASH_ON_THE_END_REGEX = """(.+)(\/|\/index\.html)$""".r
  val REMOVE_DOMAIN_ON_THE_BEGIN_REGEX = """^http\:\/\/[^\/]+\/?(.*)""".r
  val GET_BASE_REFERER_REGEX = """^"(http://vipc.cn/?|/|-|(http\s?://[^/]+)/)([^\?]*).*"$""".r
  val GET_TIMES_REGEX = """\d+\.\d{3}""".r
  val nginxDateFormat = new SimpleDateFormat("[dd/MMM/yyyy:hh:mm:ss Z]", Locale.ENGLISH)
  private val keys = List("","null","-")
  private def validKey(key: String) = !keys.contains(key)

  def parseQueryString(queryString: String): Map[String, String] = {
    if (queryString.length > 0) {
      queryString.split("&").foldLeft(Map[String,String]())((res, item) => {
        val r = item.split("=")
        if (r.length > 1 && validKey(r(0))) res + (r(0)->r(1)) else res
      })
    } else Map()
  }

  def parse(logStr: String): NginxLog = {
    val globalResult = PARSER_GLOBAL_REGEX.findAllIn(logStr).toArray
    if (globalResult.length < 11) {
      println(s"line arguments less then 11: $logStr")
      return null
    }
    val ip:String = globalResult(0)
    val (method, url, queryString, protocol) = globalResult(4) match {
      case PARSER_REQUEST_REGEX(method, url, queryString, protocol) => (method, url, queryString, protocol)
      case PARSER_REQUEST_REGEX(method, url, _, protocol) => (method, url, "", protocol)
    }
    
    val referer = globalResult(7)
    val baseReferer = referer match {
      case GET_BASE_REFERER_REGEX(host, frHost, url) =>
        val res = (host, frHost, url) match {
          case ("-", _, _) => "-"
          case (host, null, url) => s"/$url"
          case (_, frHost, url) => s"$frHost/$url"
        }
        res match {
          case REMOVE_SLASH_ON_THE_END_REGEX(result) => s"$result"
          case _ => res
        }
      case _ => referer
    }
    val query = parseQueryString(queryString)
    val accessTime = new DateTime(nginxDateFormat.parse(globalResult(3)))
    val baseUrl = url match {
      case REMOVE_SLASH_ON_THE_END_REGEX(res1, res2) => 
        res1 match {
          case REMOVE_DOMAIN_ON_THE_BEGIN_REGEX(result) => s"/$result"
          case _ => res1
        }
      case _ => url
    }
    val fullUrl = if (queryString.length > 0) s"$url?$queryString" else url
    val userAgent = globalResult(8)
    val statusCode = globalResult(5).toInt
    val responseContentLength = globalResult(6).toInt
    val cookies = parseQueryString(globalResult(10))
    val (requestTimeResult, upstreamResponseTimesResult) = GET_TIMES_REGEX.findAllIn(globalResult(11)).toArray.splitAt(1)
    val requestTime = if (requestTimeResult.length > 0) requestTimeResult(0).toDouble else -1
    val upstreamResponseTimes = upstreamResponseTimesResult.map((result) => {
      if (result.toDouble > 0) result.toDouble else 0
    })
    new NginxLog(ip, accessTime, method, protocol, referer, baseReferer, userAgent, cookies, baseUrl, fullUrl, query, statusCode, responseContentLength, requestTime, upstreamResponseTimes)
  }
}
