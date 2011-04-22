package nielinjie
package focus

import org.apache.tools.ant.types.selectors.BaseExtendSelector
import java.io.File
import scala.xml._
import org.apache.tools.ant.types.Parameter
import util.data.{Converters, Params}
import util.data.Params.{LookUpResult, Failed, Success}

class ComplexSelector extends BaseExtendSelector with Util{

  def isSelected(baseDir: File, name: String, file: File): Boolean = {

   params match {
      case Success((rf, maxC, selectA)) => {
        fileToComplexity.get(name).map {
          complex =>
            logd(name + " have coverage - " + complex)
            val b = (complex > maxC)
            logd("selected - " + b)
            b
        }.getOrElse(selectA)
      }
      case Failed(message) => {
        throw new IllegalArgumentException(message)
      }
    }
  }

  lazy val params:LookUpResult[(File, Int, Boolean)]= {
    import Params._, Converters._
    val lookingUp = for {
      rf <- lookUp("reportFile").required.as[String].to[File].ensuring(f => f.exists)
      minC <- lookUp("minComplexity").required.as[String].to[Int]
      selectA <- lookUp("selectIfAbsent").as[String].to[Boolean].default(true)
    } yield (rf,minC,selectA)
    lookingUp(this.getParameters)
  }
  lazy val fileToComplexity: Map[String, Int] = {
    params match {
      case Success((rf,_,_)) => Map((XML.loadFile(rf) \\ "object").map {
        node: Node =>
          classNameToFile((node \ "name").text) -> (node \ "ncss").text.toInt
      }: _*)
      case Failed(message) => {
        throw new IllegalArgumentException(message)
      }
    }

  }

  def classNameToFile(className: String) = {
    className.replace(".", File.separator) + ".java"
  }

}