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
  test("seq should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).seq.toList should be(is)
    }
  }

  test("to[List] should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).to[List] should be(is)
    }
  }

  test("toTraversable should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).toTraversable should be(is.toTraversable)
    }
  }

  test("toIterator should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).toIterator.sameElements(is.toIterator) should be(true)
    }
  }

  test("toStream should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).toStream should be(is.toStream)
    }
  }

  test("toArray should behave as expected") {
    forAll { (is: List[Int]) =>
      ResourceIterator(is: _*).toArray should be(is)
    }
  }

}
