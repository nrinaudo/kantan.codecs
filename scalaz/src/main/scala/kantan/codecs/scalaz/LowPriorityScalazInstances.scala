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

package kantan.codecs.scalaz

import kantan.codecs.Result
import kantan.codecs.Result.{Failure, Success}
import scalaz.{Equal, Semigroup}

trait LowPriorityScalazInstances {
  implicit def resultEqual[F, S](implicit ef: Equal[F], es: Equal[S]): Equal[Result[F, S]] =
    new Equal[Result[F, S]] {
      override def equal(x: Result[F, S], y: Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ ef.equal(f1, f2)
          case Success(_)  ⇒ false
        }
        case Success(s1) ⇒ y match {
          case Failure(_) ⇒ false
          case Success(s2) ⇒ es.equal(s1, s2)
        }
      }
    }

  implicit def resultSemigroup[F, S](implicit sf: Semigroup[F], ss: Semigroup[S]): Semigroup[Result[F, S]] =
    new Semigroup[Result[F, S]] {
      override def append(x: Result[F, S], y: ⇒ Result[F, S]) = x match {
        case Failure(f1) ⇒ y match {
          case Failure(f2) ⇒ Result.Failure(sf.append(f1, f2))
          case Success(_)  ⇒ x
        }
        case Success(s1) ⇒ y match {
          case Failure(f)  ⇒ y
          case Success(s2) ⇒ Result.Success(ss.append(s1, s2))
        }
      }
    }
}
