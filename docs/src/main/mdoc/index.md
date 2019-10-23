---
layout: index
---

[![Build Status](https://travis-ci.org/nrinaudo/kantan.codecs.svg?branch=master)](https://travis-ci.org/nrinaudo/kantan.codecs)
[![codecov](https://codecov.io/gh/nrinaudo/kantan.codecs/branch/master/graph/badge.svg)](https://codecov.io/gh/nrinaudo/kantan.codecs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.codecs_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.nrinaudo/kantan.codecs_2.13)
[![Join the chat at https://gitter.im/nrinaudo/kantan.codecs](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.codecs)

kantan.codecs is a support library for the various kantan projects.

## Getting started

kantan.codecs is currently available for Scala 2.12 and 2.13.

The current version is `@VERSION@`, which can be added to your project with one or more of the following line(s)
in your SBT build file:

```scala
// Core library, included automatically if any other module is imported.
libraryDependencies += "com.nrinaudo" %% "kantan.codecs" % "@VERSION@"

// Provides scalaz type class instances.
libraryDependencies += "com.nrinaudo" %% "kantan.codecs-scalaz" % "@VERSION@"

// Provides cats type class instances.
libraryDependencies += "com.nrinaudo" %% "kantan.codecs-cats" % "@VERSION@"
```

## Motivation

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
