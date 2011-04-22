package nielinjie
package focus

import org.apache.tools.ant.types.selectors.BaseExtendSelector
import util.data.Params.MapKind
import org.apache.tools.ant.types.Parameter
import util.data.{Params, Converters}

trait Util {
  self: BaseExtendSelector =>
  val logd = {
    x: String => log(x, org.apache.tools.ant.Project.MSG_DEBUG)
  }

  import Params._, Converters._

  implicit def parametersToMapK(pa: Array[Parameter]): MapKind[String] = new MapKind[String] {
    def get(key: String): Option[Any] = {
      pa.find(_.getName == key).map(_.getValue)
    }
  }

}