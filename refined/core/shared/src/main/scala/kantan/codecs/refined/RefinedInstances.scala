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

package kantan.codecs.refined

import eu.timepit.refined.api.RefType
import eu.timepit.refined.api.Validate
import kantan.codecs.Decoder
import kantan.codecs.Encoder
import kantan.codecs.error.IsError

trait DecoderInstances {

  /** Provides a [[Decoder]] instance for any refined type such that the reference type has a [[Decoder]] instance. */
  implicit final def refinedDecoder[E, D, F, T, P, R[_, _]](implicit
    decoder: Decoder[E, D, F, T],
    validate: Validate[D, P],
    refType: RefType[R],
    t: IsError[F]
  ): Decoder[E, R[D, P], F, T] =
    decoder.emap { d =>
      refType.refine(d).left.map(err => t.fromMessage(s"Not acceptable: '$err'"))
    }

}

trait EncoderInstances {

  /** Provides an [[Encoder]] instance for any refined type such that the reference type has an [[Encoder]] instance. */
  implicit final def refinedEncoder[E, D, T, P, R[_, _]](implicit
    encoder: Encoder[E, D, T],
    refType: RefType[R]
  ): Encoder[E, R[D, P], T] =
    encoder.contramap(refType.unwrap)

}
