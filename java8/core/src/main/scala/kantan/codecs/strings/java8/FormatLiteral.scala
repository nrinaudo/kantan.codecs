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

package kantan.codecs.strings.java8

import scala.reflect.macros.blackbox.Context

final class FormatLiteral(val sc: StringContext) extends AnyVal {
  def fmt(args: Any*): Format =
    macro FormatLiteral.fmtImpl
}

// Relatively distatefull trick to get rid of spurious warnings.
trait FormatLiteralMacro {
  def fmtImpl(c: Context)(args: c.Expr[Any]*): c.Expr[Format]
}

object FormatLiteral extends FormatLiteralMacro {
  override def fmtImpl(c: Context)(args: c.Expr[Any]*): c.Expr[Format] = {
    import c.universe._

    c.prefix.tree match {
      case Apply(_, List(Apply(_, List(lit @ Literal(Constant(str: String)))))) =>
        Format.from(str) match {
          case Left(_) => c.abort(c.enclosingPosition, s"Illegal format: '$str'")
          case Right(_) =>
            reify {
              val spliced = c.Expr[String](lit).splice

              Format
                .from(spliced)
                .getOrElse(sys.error(s"Illegal format: '$spliced'"))
            }
        }

      case _ =>
        c.abort(c.enclosingPosition, "fmt can only be used on string literals")
    }
  }
}

trait ToFormatLiteral {
  implicit def toFormatLiteral(sc: StringContext): FormatLiteral =
    new FormatLiteral(sc)
}
