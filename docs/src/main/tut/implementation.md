---
layout: default
title:  "Implementing a new codec library"
section: tutorial
---

Note that this tutorial is mostly meant for myself, while writing kantan.codecs is still fresh in my mind. It's meant
to jog my memory when I write the next kantan library, and might not make much sense to anyone else.


[`Decoder`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder
[`Encoder`]:{{ site.baseurl }}/api/#kantan.codecs.Encoder
[`Codec`]:{{ site.baseurl }}/api/#kantan.codecs.Codec
[`Throwable`]:https://docs.oracle.com/javase/7/docs/api/java/lang/Throwable.html
[`Result`]:{{ site.baseurl }}/api/#kantan.codecs.Result
[`Success`]:{{ site.baseurl }}/api/#kantan.codecs.Result$$Success
[`Failure`]:{{ site.baseurl }}/api/#kantan.codecs.Result$$Failure
[`copy`]:{{ site.baseurl }}/api/#kantan.codecs.Decoder@copy[DD](f:E=>kantan.codecs.Result[F,DD]):R[DD]