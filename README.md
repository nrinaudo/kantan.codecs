# kantan.codecs

[![Build Status](https://travis-ci.org/nrinaudo/kantan.codecs.svg?branch=master)](https://travis-ci.org/nrinaudo/kantan.codecs)
[![codecov](https://codecov.io/gh/nrinaudo/kantan.codecs/branch/master/graph/badge.svg)](https://codecov.io/gh/nrinaudo/kantan.codecs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.codecs_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.codecs_2.11)
[![Join the chat at https://gitter.im/nrinaudo/kantan.codecs](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.codecs)


## Overview and warning
Existing and planned kantan libraries are heavily encoding and decoding oriented - their main purpose is to turn
raw data and decode it into useful types, or vice-versa. [kantan.xpath](https://github.com/nrinaudo/kantan.xpath), for
instance, is all about turning the results of XPath expressions into types that can be more easily manipulated than
strings or nodes.

Since all these libraries share the same underlying purpose, it's only natural they should also share a lot of data
structures, or at least *shapes* of data structures. Both [kantan.csv](https://github.com/nrinaudo/kantan.csv) and
[kantan.xpath](https://github.com/nrinaudo/kantan.xpath), for example, define a `DecodeResult` type which is essentially
the same.

kantan.codecs tries to unify these types and provide generic laws and tests for them, which both reduces code
duplication and provides a common vocabulary for all kantan libraries.

It really isn't meant to be used directly and is more of a support library for more directly useful ones.


## A note on common abstractions
I use, and plan to keep on using, both [scalaz](https://github.com/scalaz/scalaz) and
[cats](https://github.com/typelevel/cats), and do not wish to make a final choice between the two. This means that
kantan.codecs doesn't either, and cannot use the abstractions and data types found in either of them.

This is something I can live with for the most part, except for `\/` or `Xor`. I can't bring myself to work with the
standard `Either` to encode the result of operations that can fail. This has led to the creation of `Result`, which is
kind of `\/`, kind of `Xor`, and a (very) little bit its own thing.

Does the world really need a *fourth* disjunction type? Probably not. But I did.
