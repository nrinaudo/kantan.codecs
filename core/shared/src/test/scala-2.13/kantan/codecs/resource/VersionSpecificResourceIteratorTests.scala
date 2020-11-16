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

package kantan.codecs.resource

trait VersionSpecificResourceIteratorTests { self: ResourceIteratorTests =>
  test("to(List) should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).to(List) should be(is)
    }
  }

  test("iterator should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).iterator.sameElements(is.iterator) should be(true)
    }
  }

  test("zipWithIndex should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).zipWithIndex.toList should be(is.zipWithIndex)
    }
  }

  test("scanLeft should behave as expected") {
    forAll { (is: List[String], z: Int, f: (Int, String) => Int) =>
      ResourceIterator(is: _*).scanLeft(z)(f).toList should be(is.scanLeft(z)(f))
    }
  }

  test("slice should behave as expected") {
    forAll { (is: List[Int], from: Int, to: Int) =>
      ResourceIterator(is: _*).slice(from, to).toList should be(is.slice(from, to))
    }
  }

  // Regression test for a rare (?!) random property failure.
  test("slice should behave properly when until is MinValue") {
    ResourceIterator(1, 2, 3).slice(1, Int.MinValue).hasNext should be(false)
  }

}
