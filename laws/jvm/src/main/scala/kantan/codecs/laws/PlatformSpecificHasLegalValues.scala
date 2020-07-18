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

import java.io.File
import java.net.URL
import java.nio.file.Path
import kantan.codecs.laws.HasLegalValues._

trait PlatformSpecificHasLegalValues {

  implicit val url: HasLegalStringValues[URL]   = fromToString
  implicit val path: HasLegalStringValues[Path] = fromToString
  implicit val file: HasLegalStringValues[File] = fromToString

}
