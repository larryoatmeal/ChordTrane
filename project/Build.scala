import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "ChordTrane"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "org.scalaz" % "scalaz-core_2.10" % "7.0.3"
    )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here 
    //import models._ 
    initialCommands := """
      |// make app resources accessible
      |Thread.currentThread.setContextClassLoader(getClass.getClassLoader)
      |new play.core.StaticApplication(new java.io.File("."))
      |import models._
      |import scala.collection.JavaConversions._ 
      """.stripMargin    
  )

}
