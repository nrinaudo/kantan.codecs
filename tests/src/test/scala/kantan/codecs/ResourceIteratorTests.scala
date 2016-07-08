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

package kantan.codecs

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ResourceIteratorTests extends FunSuite with GeneratorDrivenPropertyChecks {
  test("drop should drop the expected number of elements") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(ResourceIterator(is:_*).drop(n).toList == is.drop(n))
    }
  }

  test("dropWhile should drop the expected elements") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).dropWhile(f).toList == is.dropWhile(f))
    }
  }

  test("take should take the expected number of elements") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(ResourceIterator(is:_*).take(n).toList == is.take(n))
    }
  }

  test("iterators created by take should fail when too many elements are requested") {
    forAll { (is: List[Int], n: Int) ⇒
      val res = ResourceIterator(is:_*).take(n)
      while(res.hasNext) res.next()
      intercept[NoSuchElementException] { res.next() }
      ()
    }
  }

  test("collect should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Option[String]) ⇒
      val partial = Function.unlift(f)
      assert(ResourceIterator(is:_*).collect(partial).toList == is.collect(partial))
    }
  }

  test("iterators created by collect should fail when too many elements are requested") {
    forAll { (is: List[Int], f: Int ⇒ Option[String]) ⇒
      val res = ResourceIterator(is:_*).collect(Function.unlift(f))
      while(res.hasNext) res.next()
      intercept[NoSuchElementException] { res.next() }
      ()
    }
  }

  test("map should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ String) ⇒
      assert(ResourceIterator(is:_*).map(f).toList == is.map(f))
    }
  }

  test("flatMap should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ List[String]) ⇒
      assert(ResourceIterator(is:_*).flatMap(f.andThen(ss ⇒ ResourceIterator(ss:_*))).toList == is.flatMap(f))
    }
  }

  test("filter should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).filter(f).toList == is.filter(f))
    }
  }

  test("withFilter should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).withFilter(f).toList == is.iterator.withFilter(f).toList)
    }
  }

  test("seq should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).seq.toList == is)
    }
  }

  test("the empty iterator should have a definite size") {
    assert(ResourceIterator.empty.hasDefiniteSize)
  }

  test("the empty iterator should not have a next element") {
    assert(!ResourceIterator.empty.hasNext)
  }

  test("the empty iterator should throw when next is called") {
    intercept[NoSuchElementException] { ResourceIterator.empty.next() }
    ()
  }

  test("non-empty iterators should not have a definite size") {
    forAll { (i: Int, is: List[Int]) ⇒
      assert(!ResourceIterator(i :: is :_*).hasDefiniteSize)
    }
  }

  test("forall should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).forall(f) == is.forall(f))
    }
  }

  test("isEmpty should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).isEmpty == is.isEmpty)
    }
  }

  test("find should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).find(f) == is.find(f))
    }
  }

  test("exists should behave as expected") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).exists(f) == is.exists(f))
    }
  }

  test("isTraversableAgain should return false") {
    forAll { (is: List[Int]) ⇒
      assert(!ResourceIterator(is:_*).isTraversableAgain)
    }
  }

  test("toTraversable should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).toTraversable.toList == is)
    }
  }

  test("toIterator should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).toIterator.sameElements(is.toIterator))
    }
  }

  test("toStream should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).toStream == is.toStream)
    }
  }

  test("to[List] should behave as expected") {
    forAll { (is: List[Int]) ⇒
      assert(ResourceIterator(is:_*).to[List] == is)
    }
  }

  test("withClose should register the close function properly") {
    forAll { (is: List[Int]) ⇒
      var closed = false
      val res = ResourceIterator(is:_*).withClose(() ⇒ closed = true)
      while(res.hasNext) res.next()
      assert(closed)
    }
  }

  test("a safe iterator should wrap 'next on empty' errors") {
    forAll { (is: List[Int]) ⇒
      var closed = false
      val error: Throwable = new NoSuchElementException
      val res = ResourceIterator(is:_*).withClose(() ⇒ closed = true).safe(error)(identity)
      while(res.hasNext) assert(res.next().isSuccess)
      assert(res.next() == Result.Failure(error))
      assert(closed)
    }
  }

  implicit def failingIterator[A](implicit arbA: Arbitrary[A]): Gen[Iterator[A]] = for {
    as    ← Gen.nonEmptyListOf(arbA.arbitrary)
    index ← Gen.choose(0, as.length - 1)
  } yield as.iterator.zipWithIndex.map { case (a, i) ⇒ if(i == index) sys.error("failed!") else a }


  test("a safe iterator should wrap errors in next") {
    forAll(failingIterator[Int]) { is ⇒
      var closed  = false

      val res = ResourceIterator.fromIterator(is).withClose(() ⇒ closed = true)
        .safe(sys.error("eos"))(identity)

      while(res.hasNext) {
        if(res.next().isFailure) assert(!res.hasNext)
      }
      assert(closed)
    }
  }

  test("A closed iterator should not have next elements") {
    forAll { is: List[Int] ⇒
      val res = ResourceIterator(is:_*)
      res.close()
      assert(!res.hasNext)
    }
  }
}
