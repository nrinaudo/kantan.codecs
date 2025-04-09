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

/** Type class that represents data types that have an "empty" value.
  *
  * The purpose of this type class is to allow automatic derivation of [[Decoder]] for decoded types that might not have
  * a value - `Option`, `List`...
  *
  * In theory, there should rarely be a need to interact directly with this type class, and one is usually better served
  * by obtaining the [[Decoder]] instance for `Option` and mapping on it.
  */
trait Optional[A] extends Serializable {
  def empty: A
  def isEmpty(a: A): Boolean =
    a == empty
}

object Optional {
  def apply[A](implicit ev: Optional[A]): Optional[A] =
    ev

  def apply[A](a: A): Optional[A] =
    new Optional[A] {
      override val empty = a
    }

  implicit val optString: Optional[String] = Optional("")
  implicit def optSeq[A]: Optional[Seq[A]] =
    Optional(Seq.empty[A])
  implicit def optOption[A]: Optional[Option[A]] =
    Optional(Option.empty[A])
}
