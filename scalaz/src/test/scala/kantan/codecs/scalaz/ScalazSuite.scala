package kantan.codecs.scalaz

import org.scalacheck.Properties
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.typelevel.discipline.scalatest.Discipline

class ScalazSuite extends FunSuite with GeneratorDrivenPropertyChecks with Discipline {
  // The i bit is a dirty hack to work around the fact that some scalaz properties have duplicated identifiers, which
  // causes scalatest to refuse to even consider working.
  def checkAll(name: String, props: Properties): Unit = {
    var i = 0
    for((id, prop) ← props.properties) {
      i = i + 1
      test(name + "." + id + "." + i) {
        check(prop)
      }
    }
  }
}

