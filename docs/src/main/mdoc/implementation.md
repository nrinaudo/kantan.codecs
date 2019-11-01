---
layout: tutorial
title:  "Implementing a new codec library"
section: tutorial
---

This is meant as guidelines for implementing codec libraries based on kantan.codecs. Since it's essentially meant as
a quick reminder to myself, it's probably too terse and not terribly understandable to anyone else. I'm fairly certain
no one but me will ever use kantan.codecs directly, but should I be wrong, feel free to create an issue asking for
more detailed guidelines.

## Errors
Errors should be represented as sum types, and usually provide one alternative that serves as a wrapper for
[`Throwable`]. By convention, the sum type should be called `DecodeError`.

For example:

```scala mdoc:silent
sealed abstract class DecodeError extends Product with Serializable
case class TypeError(cause: Throwable) extends DecodeError
case class OutOfBounds(index: Int) extends DecodeError

// Declares creation methods - TypeError(exception) is of type TypeError, DecodeError.typeError(exception) is of
// type DecodeError.
object DecodeError {
  def typeError(cause: Throwable): DecodeError = TypeError(cause)
  def outOfBounds(index: Int): DecodeError = OutOfBounds(index)
}
```

[`Decoder`] instances will be specialised on that error type: they will return instances of `Either[DecodeError, A]`.
It's good form to "hide" `Either` as much as possible though, and a type alias should be declared:

```scala mdoc:silent
import kantan.codecs._

type DecodeResult[A] = Either[DecodeError, A]
```

Additionally, a singleton object for the specialised result type should be created to provide instance creation
methods:

```scala mdoc:silent
object DecodeResult {
  def apply[A](a: => A): DecodeResult[A] = ResultCompanion.nonFatal(TypeError.apply)(a)
  def success[A](a: A): DecodeResult[A] = Right(a)
  def outOfBounds(index: Int): DecodeResult[Nothing] = Left(OutOfBounds(index))
}
```

## `Encoder`, `Decoder`, `Codec`

### Tag type
[`Encoder`], [`Decoder`] and [`Codec`] require a tag type - a phantom type use to disambiguate between implementations
that work with the same encoded type. This is traditionally a singleton object called `codecs`.

```scala mdoc:silent
 // We'll need to redefine that later to add default instances.
object codecs
```

### Companion objects

Specialised types should be declared as type aliases:

```scala mdoc:silent
type CellDecoder[A] = Decoder[String, A, DecodeError, codecs.type]
type CellEncoder[A] = Encoder[String, A, codecs.type]
type CellCodec[A] = Codec[String, A, DecodeError, codecs.type]
```

Each specialised type should have a singleton object, used to declare creation and summoning methods:

```scala mdoc:silent
object CellDecoder {
  def apply[A](implicit da: CellDecoder[A]): CellDecoder[A] = da
  def from[A](f: String => DecodeResult[A]): CellDecoder[A] = Decoder.from(f)
}

object CellEncoder {
  def apply[A](implicit ea: CellEncoder[A]): CellEncoder[A] = ea
  def from[A](f: A => String): CellEncoder[A] = Encoder.from(f)
}

object CellCodec {
  def apply[A](implicit ca: CellCodec[A]): CellCodec[A] = ca
  def from[A](f: String => DecodeResult[A])(g: A => String): CellCodec[A] = Codec.from(f)(g)
}
```

### Default instances
[`Decoder`] and [`Encoder`] implementations should have accompanying "instances" trait containing all default instances.

```scala mdoc:silent
trait CellDecoderInstances {
  val stringDecoder: CellDecoder[String] = CellDecoder.from(DecodeResult.success)
  // ...
}

trait CellEncoderInstances {
  val stringEncoder: CellEncoder[String] = CellEncoder.from(identity)
  // ...
}
```

[`Codec`] implementations should have an accompanying "instances" trait that extends the previously defined ones:

```scala mdoc:silent
trait CellCodecInstances extends CellDecoderInstances with CellEncoderInstances
```

Finally, in order for all these instances to be available in the implicit scope, `codecs` should be modified to extend
`CellCodecInstances`.

```scala
object codecs extends CellCodecInstances
```

## Notes on default instances

### Adapting existing instances
Decoding from strings is a fairly common requirement, regardless of the underlying format. Default codecs are provided
for these, and can be adapted trivially:

```scala mdoc:silent
import kantan.codecs.strings.{StringEncoder, StringDecoder}

def fromStringDecoder[A](implicit da: StringDecoder[A]): CellDecoder[A] =
  da.leftMap(DecodeError.typeError).tag[codecs.type]

def fromStringEncoder[A](implicit ea: StringEncoder[A]): CellEncoder[A] = ea.tag[codecs.type]
```

### Difference between primitive types and first-order types
There's a critical distinction to be made between default instances for primitive types and for first-order types:
it's safe to provide a [`Codec`] for the former, but usually not for the later.

In order to write default instances for `Option`, one might be tempted to write:

```scala mdoc:silent
def optionCodec[A](implicit ca: CellCodec[A]): CellCodec[Option[A]] = CellCodec.from { s =>
  if(s.isEmpty) DecodeResult.success(Option.empty[A])
  else          ca.decode(s).map(Some.apply)
} { _.map(ca.encode).getOrElse("") }
```

This is bad, for two reasons. The first, most obvious one is that we should *never* require a [`Codec`] instance.
When a function needs to both encode and decode a type, it should require an instance of [`Encoder`] and one of
[`Decoder`] instead.

The second reason this is a bad idea, even if one where to "split" the codec into its encoder and decoder halves, is
that any type `A` that has, say, an [`Encoder`] but not a [`Decoder`] should still get a free `Encoder[Option[A]]`, but
this implementation prevents it.

### Naming default instances
Since encoder and decoder instances will eventually find themselves part of the same trait (`CellCodecInstances` in our
examples), it's important to make sure their names don't clash: prefer `stringDecoder` and `stringEncoder` to the
simpler but unsafe `string`.


[`Decoder`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder
[`Encoder`]:{{ site.baseurl }}/api/#kantan.codecs.Encoder
[`Codec`]:{{ site.baseurl }}/api/#kantan.codecs.Codec
[`Throwable`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Throwable.html
[`copy`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder@copy[DD](f:E=>kantan.codecs.Result[F,DD]):R[DD]
