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

package kantan.codecs.collection

import kantan.codecs.laws.discipline.SerializableTests

import scala.collection.immutable.Queue
import scala.collection.immutable.TreeSet

class SerializationTests extends VersionSpecificSerializationTests {
  checkAll("Factory[TreeSet]", SerializableTests[Factory[Int, TreeSet[Int]]].serializable)
  checkAll("Factory[QueueFactoryTests]", SerializableTests[Factory[Int, Queue[Int]]].serializable)
  checkAll("Factory[ListFactoryTests]", SerializableTests[Factory[Int, List[Int]]].serializable)
  checkAll("Factory[VectorFactoryTests]", SerializableTests[Factory[Int, Vector[Int]]].serializable)
  checkAll("Factory[IndexedSeqFactoryTests]", SerializableTests[Factory[Int, IndexedSeq[Int]]].serializable)
  checkAll("Factory[SeqFactoryTests]", SerializableTests[Factory[Int, Seq[Int]]].serializable)
  checkAll("Factory[SetFactoryTests]", SerializableTests[Factory[Int, Set[Int]]].serializable)
  checkAll("Factory[ArrayFactoryTests]", SerializableTests[Factory[Int, Array[Int]]].serializable)

}
