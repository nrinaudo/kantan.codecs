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
package resource
package bom

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import org.apache.commons.io.{ByteOrderMark => BOM}
import org.apache.commons.io.input.BOMInputStream
import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import scala.io.Codec

/** Makes sure `BomWriter` writes BOMs as expected. */
class BomWriterTests extends FunSuite with GeneratorDrivenPropertyChecks with Matchers {
  def write(str: String, codec: Codec): BOM =
    new BOMInputStream(
      new ByteArrayInputStream(InMemoryBomWriter.write(str, codec)),
      BOM.UTF_8,
      BOM.UTF_16BE,
      BOM.UTF_16LE,
      BOM.UTF_32BE,
      BOM.UTF_32LE
    ).getBOM

  test("UTF-8 BOMs should be written properly") {
    forAll { str: String =>
      write(str, Codec.UTF8) should be(BOM.UTF_8)
    }
  }

  test("UTF-16LE BOMs should be written properly") {
    forAll { str: String =>
      write(str, Codec(Charset.forName("UTF-16LE"))) should be(BOM.UTF_16LE)
    }
  }

  test("UTF-16BE BOMs should be written properly") {
    forAll { str: String =>
      write(str, Codec(Charset.forName("UTF-16BE"))) should be(BOM.UTF_16BE)
    }
  }

  test("UTF-32LE BOMs should be written properly") {
    forAll { str: String =>
      write(str, Codec(Charset.forName("UTF-32LE"))) should be(BOM.UTF_32LE)
    }
  }

  test("UTF-32BE BOMs should be written properly") {
    forAll { str: String =>
      write(str, Codec(Charset.forName("UTF-32BE"))) should be(BOM.UTF_32BE)
    }
  }
}
