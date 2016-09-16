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

import imp.imp
import kantan.codecs.resource.ResourceIterator
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.FunSuite
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ResourceIteratorTests extends FunSuite with GeneratorDrivenPropertyChecks {
  // - Tools -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  case class FailingIterator[A](iterator: Iterator[A], index: Int) {
    def resourceIterator: ResourceIterator[A] = new ResourceIterator[A] {
      var i = 0

      def checkFail(): Unit = {
        if(i == index) sys.error("failed")
        i += 1
      }

      override def readNext() = {
        checkFail()
        iterator.next()
      }

      override def checkNext = {
        checkFail()
        iterator.hasNext
      }
      override def release() = ()
    }
  }

  implicit def arbFailingIterator[A: Arbitrary]: Arbitrary[FailingIterator[A]] = Arbitrary {
    for {
      as    ← Gen.nonEmptyListOf(imp[Arbitrary[A]].arbitrary)
      index ← Gen.choose(0, 2 * (as.length - 1))
    }  yield FailingIterator(as.iterator, index)
  }


  def closedWhenEmpty[A](r: ResourceIterator[A]): Boolean = {
    var closed = false
    val r2 = r.withClose(() ⇒ closed = true)
    while(r2.hasNext) r2.next()
    closed
  }



  // - Tests -----------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  test("the empty resource iterator should have its close method called") {
    assert(closedWhenEmpty(ResourceIterator.empty))
  }

  test("drop should drop the expected number of elements") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(ResourceIterator(is:_*).drop(n).toList == is.drop(n))
    }
  }

  test("safe 'dropped' iterators should close properly and not leak exceptions") {
    forAll { (is: FailingIterator[Int], n: Int) ⇒
      assert(closedWhenEmpty(is.resourceIterator.drop(n).safe(new Throwable("eos"))(identity)))
    }
  }

  test("'dropWhiled' iterators should have their close method called when empty") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(closedWhenEmpty(ResourceIterator(is:_*).dropWhile(f)))
    }
  }

  test("safe 'dropWhiled' iterators should close properly and not leak exceptions") {
    forAll { (is: FailingIterator[Int], f: Int ⇒ Boolean) ⇒
      assert(closedWhenEmpty(is.resourceIterator.dropWhile(f).safe(new Throwable("eos"))(identity)))
    }
  }

  test("'dropped' iterators should have their close method called when empty") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(closedWhenEmpty(ResourceIterator(is:_*).drop(n)))
    }
  }

  test("dropWhile should drop the expected elements") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).dropWhile(f).toList == is.dropWhile(f))
    }
  }

  test("takeWhile should take the expected elements") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(ResourceIterator(is:_*).takeWhile(f).toList == is.takeWhile(f))
    }
  }

  test("'takeWhiled' iterators should have their close method called when empty") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(closedWhenEmpty(ResourceIterator(is:_*).takeWhile(f)))
    }
  }

  test("safe 'takeWhiled' iterators should close properly and not leak exceptions") {
    forAll { (is: FailingIterator[Int], f: Int ⇒ Boolean) ⇒
      assert(closedWhenEmpty(is.resourceIterator.takeWhile(f).safe(new Throwable("eos"))(identity)))
    }
  }

  test("take should take the expected number of elements") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(ResourceIterator(is:_*).take(n).toList == is.take(n))
    }
  }

  test("'taken' iterators should have their close method called when empty") {
    forAll { (is: List[Int], n: Int) ⇒
      assert(closedWhenEmpty(ResourceIterator(is:_*).take(n)))
    }
  }

  test("safe 'taken' iterators should close properly and not leak exceptions") {
    forAll { (is: FailingIterator[Int], n: Int) ⇒
      assert(closedWhenEmpty(is.resourceIterator.take(n).safe(new Throwable("eos"))(identity)))
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

  test("filtered iterators should close when empty") {
    forAll { (is: List[Int], f: Int ⇒ Boolean) ⇒
      assert(closedWhenEmpty(ResourceIterator(is:_*).filter(f)))
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
      assert(closedWhenEmpty(ResourceIterator(is:_*)))
    }
  }

  test("a safe iterator should wrap 'next on empty' errors") {
    forAll { (is: List[Int]) ⇒
      val error: Throwable = new NoSuchElementException

      val res = ResourceIterator(is:_*).safe(error)(identity)
      assert(closedWhenEmpty(res))
      assert(res.next() == Result.Failure(error))
    }
  }

  test("a safe iterator should wrap errors in next") {
    forAll { is: FailingIterator[Int] ⇒
      var closed  = false

      val res = is.resourceIterator.withClose(() ⇒ closed = true)
        .safe(new Throwable("eos"))(identity)

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
