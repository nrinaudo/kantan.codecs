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

package kantan.codecs.shapeless.laws

import kantan.codecs.laws.{HasIllegalValues, HasLegalValues}

sealed trait Or[+A, +B]         extends Product with Serializable
final case class Left[A](a: A)  extends Or[A, Nothing]
final case class Right[B](b: B) extends Or[Nothing, B]

object Or {
  def orToEither[L, R](or: Or[L, R]): Either[L, R] = or match {
    case Left(l)  => scala.util.Left(l)
    case Right(r) => scala.util.Right(r)
  }

  implicit def hasLegalValues[E, L, R, T](
    implicit hl: HasLegalValues[E, Either[L, R], T]
  ): HasLegalValues[E, Or[L, R], T] =
    HasLegalValues.from { or =>
      hl.encode(orToEither(or))
    }

  implicit def hasIllegalValues[E, L, R, T](
    implicit hi: HasIllegalValues[E, Either[L, R], T]
  ): HasIllegalValues[E, Or[L, R], T] =
    new HasIllegalValues[E, Or[L, R], T] {
      override def isValid(e: E) = hi.isValid(e)
      override def perturb(e: E) = hi.perturb(e)
    }
}
