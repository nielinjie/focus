package nielinjie
package focus


import org.apache.tools.ant.types.selectors.BaseExtendSelector
import java.io.File
import scala.xml._
import org.apache.tools.ant.types.Parameter
import util.data.{Converters, Params}
import util.io.XMLUtil
import util.data.Params.{Failed, Success, LookUpResult}

class CoverageSelector extends BaseExtendSelector with Util {

  def isSelected(baseDir: File, name: String, file: File): Boolean = {
    params match {
      case Success((rf, maxC, selectA)) => {
        fileToCoverage.get(name).map {
          cover =>
            logd(name + " have coverage - " + cover)
            val b = (cover < maxC)
            logd("selected - " + b)
            b
        }.getOrElse(selectA)
      }
      case Failed(message) => {
        throw new IllegalArgumentException(message)
      }
    }
  }

  lazy val params: LookUpResult[(File, Int, Boolean)] = {
    import Params._, Converters._
    val lookingUp = for {
      rf <- lookUp("reportFile").required.as[String].to[File].ensuring(f => f.exists, "report file does not exist")
      maxC <- lookUp("maxLineCoverage").required.as[String].to[Int]
      selectA <- lookUp("selectIfAbsent").as[String].to[Boolean].default(true)
    } yield (rf, maxC, selectA)
    lookingUp(this.getParameters)
  }
  lazy val fileToCoverage: Map[String, Int] = {
    params match {
      case Success((rf, _, _)) =>
        Map(
          (XML.loadFile(rf) \\ "package").map {
            packageNode: Node =>
              val packageName = packageNameToFile((packageNode \ "@name").text)
              (packageNode \\ "srcfile").map {
                srcNode: Node =>
                  val fileName = packageName + File.separator + (srcNode \ "@name").text
                  (srcNode \ "coverage").filter {
                    cov =>
                      (cov \ "@type").text.startsWith("line")
                  }.map {
                    x =>
                      fileName -> (x \ "@value").text.takeWhile(_ != '%').toInt
                  }
              }.flatten
          }.flatten: _*)
      case Failed(message) => {
        throw new IllegalArgumentException(message)
      }
    }
  }

  def packageNameToFile(packageName: String) = {
    packageName.replace(".", File.separator)
  }

}