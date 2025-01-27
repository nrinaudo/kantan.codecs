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

package kantan.codecs.laws

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import scala.util.Try

/** Laws for serializable type class instances. */
trait SerializableLaws[A] {
  def value: A

  def serialize(o: Any): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    val oos  = new ObjectOutputStream(baos)

    oos.writeObject(o)
    oos.flush()
    baos.toByteArray
  }

  def deserialize(data: Array[Byte]): Any = {
    val bais = new ByteArrayInputStream(data)
    val ois  = new ObjectInputStream(bais)
    ois.readObject
  }

  def serializable(): Boolean =
    Try(deserialize(serialize(value))).isSuccess
}

object SerializableLaws {
  implicit def apply[A](implicit a: A): SerializableLaws[A] =
    new SerializableLaws[A] {
      override def value =
        a
    }
}
