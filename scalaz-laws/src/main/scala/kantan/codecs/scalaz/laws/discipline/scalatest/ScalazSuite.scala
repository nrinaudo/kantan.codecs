/*
 * Copyright 2016 Nicolas Rinaudo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kantan.codecs.scalaz.laws.discipline.scalatest

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
      test(s"$name[$i].$id") {
        check(prop)
      }
    }
  }
}
