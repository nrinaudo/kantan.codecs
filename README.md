# kantan.codecs

[![Build Status](https://github.com/nrinaudo/kantan.codecs/actions/workflows/cli.yml/badge.svg?branch=master)](https://github.com/nrinaudo/kantan.codecs/actions/workflows/cli.yml?branch=master)
[![kantan.codecs Scala version support](https://index.scala-lang.org/nrinaudo/kantan.codecs/kantan.codecs/latest.svg)](https://index.scala-lang.org/nrinaudo/kantan.codecs/kantan.codecs)
[![Join the chat at https://gitter.im/nrinaudo/kantan.codecs](https://img.shields.io/badge/gitter-join%20chat-52c435.svg)](https://gitter.im/nrinaudo/kantan.codecs)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

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
