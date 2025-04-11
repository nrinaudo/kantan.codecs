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

import scala.collection.BuildFrom
import scala.collection.mutable

object VersionSpecificResultCompanion {
  trait Simple[F] {

    /** Turns a collection of results into a result of a collection. */
    @inline def sequence[S, M[X] <: IterableOnce[X]](rs: M[Either[F, S]])(implicit
      bf: BuildFrom[M[Either[F, S]], S, M[S]]
    ): Either[F, M[S]] =
      rs.iterator
        .foldLeft(Right(bf.newBuilder(rs)): Either[F, mutable.Builder[S, M[S]]]) { (builder, res) =>
          for {
            b <- builder
            r <- res
          } yield b += r
        }
        .map(_.result())
  }
}
