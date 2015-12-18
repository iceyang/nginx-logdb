package net.iceyang.model

import com.github.nscala_time.time.Imports._

case class NginxLog(
  val ip: String,
  val accessTime: DateTime,
  val method: String,
  val protocol: String,
  val referer: String,
  val baseReferer: String,
  val userAgent: String,
  val cookies: Map[String, String],
  val baseUrl: String,
  val fullUrl: String,
  val query: Map[String, String],
  val statusCode: Int,
  val responseContentLength: Int,
  val requestTime: Double,
  val upstreamResponseTimes: Array[Double]) {
    def queryString = if (query.size != 0) (for ((k, v) <- query) yield s"$k=$v").reduceLeft(_ + "&" + _) else ""
    def cookieString = if (cookies.size != 0) (for ((k, v) <- cookies) yield s"$k=$v").reduceLeft(_ + "&" + _) else ""

    override def toString(): String = s"""$ip - - [$accessTime] "$method $fullUrl $protocol" $statusCode $responseContentLength "$referer" "$userAgent" "-" ${cookieString}"""
}
