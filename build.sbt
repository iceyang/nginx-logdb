lazy val commonSetting = Seq(
  version := "1.0",
  scalaVersion := "2.11.7",
  organization := "net.iceyang"
)

libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.6.0",
  "joda-time" % "joda-time" % "2.9.1",
  "org.reactivemongo" %% "reactivemongo" % "0.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSetting: _*).
  settings(
    name := "nginx-logdb"
  )
