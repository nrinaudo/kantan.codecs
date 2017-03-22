/*
 * Copyright 2017 Nicolas Rinaudo
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

trait ResourceIterable[A] extends Traversable[A] {
  def iterator: ResourceIterator[A]
  override def foreach[U](f: A ⇒ U) = iterator.foreach(f)
  override def forall(p: A ⇒ Boolean) = iterator.forall(p)
  override def exists(p: A ⇒ Boolean) = iterator.exists(p)
  override def find(p: A ⇒ Boolean) = iterator.find(p)
  override def isEmpty = !iterator.hasNext
  override def foldRight[B](z: B)(op: (A, B) ⇒ B) = iterator.foldRight(z)(op)
  override def reduceRight[B >: A](op: (A, B) ⇒ B) = iterator.reduceRight(op)
}
