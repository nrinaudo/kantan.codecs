---
layout: default
title:  "Implementing a new codec library"
section: tutorial
---

Note that this tutorial is mostly meant for myself, while writing kantan.codecs is still fresh in my mind. It's meant
to jog my memory when I write the next kantan library, and might not make much sense to anyone else.

This takes the simple example of a codec system meant to extract primitive types from strings.

## Step 1: error cases

Before we can think of writing a [`Decoder`] implementation, we need to think how it can fail. Not necessarily because
it's good practice (it is), but mostly because the error cases are part of a [`Decoder`] implementation's signature.

In our case, there's really only one thing that can go wrong: the content of the string to parse is not legal -
`foobar` for an int, for example. Let's call that a `ParseError`. We might want to give it a [`Throwable`] field, in case
the actual error is interesting, not just the fact that it occurred:

```tut:silent
case class ParseError(cause: Throwable)
```

Knowing how our decoders will fail tells us the type of a decoding result: decoding to an `A` is going to yield a
[`Result`]`[ParseError, A]`. This is cumbersome to manipulate though - the first type parameter is always going to be a
`ParseError` and users of our decoders shouldn't have to put it in every time. It's good practice to make a type alias:

```tut:silent
import kantan.codecs._
type ParseResult[A] = Result[ParseError, A]
```

Additionally, it's good practice to provide helpers for `ParseResult` instance creation - ideally, users of our library
should never have to know that the underlying result type really is a [`Result`].

The most important creation helper is the one that takes an expression and evaluates it into a [`Success`], but yields
a [`Failure`] if an exception is thrown. This is typically the `apply` method of the companion object:

```tut:silent
object ParseResult {
  def apply[A](a: => A): ParseResult[A] = Result.nonFatal(a).leftMap(ParseError.apply)
}
```

Another helpful creation method is `success`, for cases that cannot fail and don't need the added cost of being wrapped
in a `try / catch` block:

```tut:silent
def success[A](a: A): ParseResult[A] = Result.success(a)
```

When applicable, similar methods can be declared for common failure cases.


## Step 2: decoder
A [`Decoder`] implementation usually bears the name of what it decodes - our example decodes strings, so it would be
called a `StringDecoder`.

The basic skeleton for a decoder is always the same: specialise the type parameters and provide a default implementation
for [`copy`] (usually through the `apply` method on the decoder's companion object). For our example:

```tut:silent
trait StringDecoder[A] extends Decoder[String, A, ParseError, StringDecoder] {
  override protected def copy[AA](f: String => ParseResult[AA]) = new StringDecoder[AA] {
    override def decode(s: String) = f(s)
  }
}
```

The companion object should usually declare three instance creation methods:

* `apply`, which takes a function from encoded values to result types and create the corresponding decoder.
* `fromSafe`, which takes a function from encoded values to decoded types and creates the corresponding decoder, with
  the understanding that the specified function cannot throw.
* `fromUnsafe`, which is similar to `fromSafe` but expects decode failures to throw and turns exceptions into valid
  failure types.

Default instances should be provided and declared in the companion object:

```tut:silent
implicit val stringDecoder: StringDecoder[String] = new StringDecoder[String] {
  override def decode(s: String) = ParseResult(s)
}
implicit val intDecoder: StringDecoder[Int] = stringDecoder.mapResult(s => ParseResult(s.toInt))
implicit val floatDecoder: StringDecoder[Float] = stringDecoder.mapResult(s => ParseResult(s.toFloat))
// ...
```

## Step 3: encoder
The [`Encoder`] bit is almost the same thing as the [`Decoder`], just slightly simpler as encoding cannot fail:

```tut:silent
trait StringEncoder[A] extends Encoder[String, A, StringEncoder] {
  override protected def copy[AA](f: AA => String) = new StringEncoder[AA] {
    override def encode(a: AA) = f(a) 
  }
}

object StringEncoder {
  implicit val stringEncoder: StringEncoder[String] = new StringEncoder[String] {
    override def encode(s: String) = s
  }
  implicit val intEncoder: StringEncoder[Int] = stringEncoder.contramap(_.toString)
  implicit val floatEncoder: StringEncoder[Float] = stringEncoder.contramap(_.toString)
  // ...
}
```

## Step 4: codec
[`Codec`]s are really just meant as a convenience for declaring encoders and decoders in one go for types that have both.
As such, they should be kept simple and just present one creation method in the companion object:

```tut:silent
trait StringCodec[A] extends Codec[String, A, ParseError, StringDecoder, StringEncoder] with StringDecoder[A] with StringEncoder[A]

object StringCodec {
  def apply[A](f: String => ParseResult[A])(g: A => String): StringCodec[A] = new StringCodec[A] {
    override def decode(s: String) = f(s)
    override def encode(a: A) = g(a)
  }
}
```

Any method that derives a [`Codec`] instance from existing [`Decoder`] and [`Encoder`] instances should be avoided, as
they give the impression that a [`Codec`] is a more powerful construct, when it's just a shortcut. 

[`Decoder`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder
[`Encoder`]:{{ site.baseurl }}/api/#kantan.codecs.Encoder
[`Codec`]:{{ site.baseurl }}/api/#kantan.codecs.Codec
[`Throwable`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Throwable.html
[`Result`]:{{ site.baseurl }}/api/#kantan.codecs.Result
[`Success`]:{{ site.baseurl }}/api/#kantan.codecs.Result$$Success
[`Failure`]:{{ site.baseurl }}/api/#kantan.codecs.Result$$Failure
[`copy`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder@copy[DD](f:E=>kantan.codecs.Result[F,DD]):R[DD]