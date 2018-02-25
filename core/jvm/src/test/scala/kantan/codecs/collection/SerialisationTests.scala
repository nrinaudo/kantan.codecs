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
package collection

import laws.discipline._
import scala.collection.immutable.{Queue, TreeSet}

class SerialisationTests extends DisciplineSuite {

  checkAll("HasBuilder[TreeSet]", SerializableTests[HasBuilder[TreeSet, Int]].serializable)
  checkAll("HasBuilder[QueueHasBuilderTests]", SerializableTests[HasBuilder[Queue, Int]].serializable)
  checkAll("HasBuilder[ListHasBuilderTests]", SerializableTests[HasBuilder[List, Int]].serializable)
  checkAll("HasBuilder[StreamHasBuilderTests]", SerializableTests[HasBuilder[Stream, Int]].serializable)
  checkAll("HasBuilder[VectorHasBuilderTests]", SerializableTests[HasBuilder[Vector, Int]].serializable)
  checkAll("HasBuilder[IndexedSeqHasBuilderTests]", SerializableTests[HasBuilder[IndexedSeq, Int]].serializable)
  checkAll("HasBuilder[TraversableHasBuilderTests]", SerializableTests[HasBuilder[Traversable, Int]].serializable)
  checkAll("HasBuilder[SeqHasBuilderTests]", SerializableTests[HasBuilder[Seq, Int]].serializable)
  checkAll("HasBuilder[SetHasBuilderTests]", SerializableTests[HasBuilder[Set, Int]].serializable)
  checkAll("HasBuilder[ArrayHasBuilderTests]", SerializableTests[HasBuilder[Array, Int]].serializable)

}
