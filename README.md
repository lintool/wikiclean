WikiClean
=========
[![Build Status](https://travis-ci.org/lintool/wikiclean.svg?branch=master)](https://travis-ci.org/lintool/wikiclean)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.wikiclean/wikiclean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.wikiclean/wikiclean)
[![LICENSE](https://img.shields.io/badge/license-Apache-blue.svg?style=flat-square)](http://www.apache.org/licenses/LICENSE-2.0)

WikiClean is a Java Wikipedia markup to plain text converter. It takes [Wikipedia XML dumps](http://en.wikipedia.org/wiki/Wikipedia:Database_download) with articles in wikimedia markup and generates clean plain text.

Why?
----

For text processing applications, we often need access to plain text, unadulterated by wikimedia markup. This is surprisingly non-trivial, as Wikipedia articles are full of complexities such as references, image captions, tables, infoboxes, etc., which are not useful for many applications.

Before setting out to write this package, I explored many of the Java alternatives for parsing Wikipedia pages described
[here](http://www.mediawiki.org/wiki/Alternative_parsers) and found none of them to be adequate for generating clean plain text. The primarily challenge is that most of these packages aspire to be complete Wikipedia parsers (e.g., for rendering), whereas WikiClean was designed with a much simpler goal &mdash; wiki markup to plain text conversion (nothing more, nothing less).

Usage
-----

It's simple to use WikiClean:

```
WikiClean cleaner = new WikiClean.Builder().build();
String content = cleaner.clean(raw);
```

Where `raw` is the raw Wikpedia XML.

The builder allows you to specify a few options:

* `withTitle` to specify whether to prepend the article title in the plain text.
* `withFooter` to specify whether to keep the sections "See also", "Reference", "Further reading", and "External links".

By default, both options are set to false.

Also, use `withLanguage` to set the language. Currently, 17 are supported:

* [English](https://en.wikipedia.org/wiki/) (default)
* [Breton](https://br.wikipedia.org/wiki/) (Brezhoneg - br)
* [Catalan](https://ca.wikipedia.org/wiki/) (Català - ca)
* [Chinese](https://zh.wikipedia.org/) (中文 - zh)
* [Dutch](https://nl.wikipedia.org/wiki/) (Nederlands - nl)
* [Esperanto](https://eo.wikipedia.org/wiki/) (Esperanto - eo)
* [French](https://fr.wikipedia.org/wiki/) (Français - fr)
* [Galician](https://gl.wikipedia.org/wiki/) (Galego - gl)
* [German](https://de.wikipedia.org/wiki/) (Deutsch - de)
* [Greek](https://el.wikipedia.org/wiki/) (Ελληνικά - el)
* [Italian](https://it.wikipedia.org/wiki/) (Italiano - it)
* [Kannada](https://kn.wikipedia.org/wiki/) (ಕನ್ನಡ - kn)
* [Polish](https://pl.wikipedia.org/wiki/) (Polski - pl)
* [Portuguese](https://pt.wikipedia.org/wiki/) (Português - pt)
* [Russian](https://ru.wikipedia.org/wiki/) (Русский - ru)
* [Spanish](https://es.wikipedia.org/wiki/) (Español - es)
* [Ukrainian](https://uk.wikipedia.org/wiki/) (Українська - uk)

The corresponding classes are in [`org.wikiclean.languages`](src/main/java/org/wikiclean/languages).

Contributions for providing additional language support welcome!

Putting everything together, the default builder is equivalent to:

```
WikiClean cleaner =
    new WikiClean.Builder()
        .withLanguage(new English())
        .withTitle(false)
        .withFooter(false).build();
String content = cleaner.clean(raw);
```

Sample command-line invocation to read a Wikipedia dump and output plain text:

```
mvn exec:java -Dexec.mainClass=org.wikiclean.WikipediaArticlesDump \
  -Dexec.args="-input enwiki-20161220-pages-articles.xml.bz2" | less
```

Maven Artifacts
---------------

Latest releases of Maven artifacts are available at [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cwikiclean).

License
-------

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
