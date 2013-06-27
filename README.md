WikiClean
=========

WikiClean is a Java Wikipedia markup to plain text converter. It takes [Wikipedia XML dumps](http://en.wikipedia.org/wiki/Wikipedia:Database_download) with articles in wikimedia markup and generates clean plain text.

Why?
----

For text processing applications, we often need access to plain text, unadulterated by wikimedia markup. This is surprisingly non-trivial, as Wikipedia articles are full of complexities such as references, image captions, tables, infoboxes, etc., which are not useful for many applications.

Before setting out to write this package, I explored many of the Java alternatives for parsing Wikipedia pages described
[here](http://www.mediawiki.org/wiki/Alternative_parsers) and found none of them to be adequate (in generating clean plain text). The primarily challenge is that many of these packages aspire to be complete Wikipedia parsers and renders, whereas WikiClean was designed with a much simpler goal.


Maven Artifacts
---------------

Latest releases of Maven artifacts are available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cwikiclean).

License
-------

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
