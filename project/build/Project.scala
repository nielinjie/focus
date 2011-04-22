import sbt._
class Project(info: ProjectInfo) extends DefaultProject(info) with IdeaProject{
    val scalaToolsSnapshots = "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"
//    val scalazCore = "org.scalaz" %% "scalaz-core" % "6.0-SNAPSHOT"
//    val scalazHttp = "org.scalaz" %% "scalaz-http" % "6.0-SNAPSHOT"


    //val specs = "org.specs2" %% "specs2" % "1.1-SNAPSHOT" % "test"
    val util = "nielinjie" %% "util.io" % "1.0"
    val data = "nielinjie" %% "util.data" % "1.0"
   	val ant = "org.apache.ant" % "ant" % "1.7.1" % "compile"
}


