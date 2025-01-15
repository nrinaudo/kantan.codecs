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

/** Provides a simple mechanism to insert derived type class instances in the implicit resolution mechanism.
  *
  * By default, when imported, such instances have the highest possible priority, which is typically problematic with
  * shapeless instance derivation: bespoke instances for `Option` or `Either`, for example, find themselves shadowed by
  * the generically derived ones.
  *
  * If such instances are of type [[kantan.codecs.export.DerivedDecoder]] / [[kantan.codecs.export.DerivedEncoder]]
  * rather than [[Decoder]] / [[Encoder]], however, they'll find themselves with a lower precedence and only be used if
  * no other, more specific instance is found.
  */
package object `export` {

  /** [[Decoder]] instance that should only be used when no specific one is found. */
  type DerivedDecoder[E, D, F, T] = Exported[Decoder[E, D, F, T]]

  /** [[Encoder]] instance that should only be used when no specific one is found. */
  type DerivedEncoder[E, D, T] = Exported[Encoder[E, D, T]]
}
