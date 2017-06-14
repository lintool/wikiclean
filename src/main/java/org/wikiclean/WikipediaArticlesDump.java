/**
 * WikiClean: A Java Wikipedia markup to plain text converter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wikiclean;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.wikiclean.WikiClean.WikiLanguage;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Object for reading Wikipedia articles from a bz2-compressed dump file.
 */
public class WikipediaArticlesDump implements Iterable<String> {
  private static final int DEFAULT_STRINGBUFFER_CAPACITY = 1024;

  private final BufferedReader reader;
  private final FileInputStream stream;

  /**
   * Class constructor.
   * @param file path to dump file
   * @throws IOException if any file-related errors are encountered
   */
  public WikipediaArticlesDump(File file) throws IOException {
    stream = new FileInputStream(file);
    byte[] ignoreBytes = new byte[2];
    stream.read(ignoreBytes); // "B", "Z" bytes from commandline tools
    reader = new BufferedReader(new InputStreamReader(new CBZip2InputStream(
            new BufferedInputStream(stream)), "UTF8"));
  }

  /**
   * Provides an iterator over Wikipedia articles.
   * @return an iterator over Wikipedia articles
   */
  public Iterator<String> iterator() {
    return new Iterator<String>() {
      private String nextArticle = null;

      public boolean hasNext() {
        if (nextArticle != null) {
          return true;
        }

        try {
          nextArticle = readNext();
        } catch (IOException e) {
          return false;
        }

        return nextArticle!= null;
      }

      public String next() {
        // If current article is null, try to advance.
        if (nextArticle == null) {
          try {
            nextArticle = readNext();
            // If we advance and and still get a null, then we're done.
            if (nextArticle == null) {
              throw new NoSuchElementException();
            }
          } catch (IOException e) {
            throw new NoSuchElementException();
          }
        }

        String article = nextArticle;
        nextArticle = null;
        return article;
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

      private String readNext() throws IOException {
        String s;
        StringBuilder sb = new StringBuilder(DEFAULT_STRINGBUFFER_CAPACITY);

        while ((s = reader.readLine()) != null) {
          if (s.endsWith("<page>"))
            break;
        }

        if (s == null) {
          stream.close();
          reader.close();
          return null;
        }

        sb.append(s).append("\n");

        while ((s = reader.readLine()) != null) {
          sb.append(s).append("\n");

          if (s.endsWith("</page>"))
            break;
        }

        return sb.toString();
      }
    };
  }

  /**
   * Provides a stream of Wikipedia articles.
   * @return a stream of Wikipedia articles
   */
  public Stream<String> stream() {
    return StreamSupport.stream(this.spliterator(), false);
  }

  private static final class Args {
    @Option(name = "-input", metaVar = "[path]", required = true, usage = "input path")
    File input;

    @Option(name = "-lang", metaVar = "[lang]", usage = "two-letter language code")
    String lang = "en";
  }

  /**
   * Simple program prints out all cleaned articles.
   * @param argv command-line argument
   * @throws Exception if any errors are encountered
   */
  public static void main(String[] argv) throws Exception {
    final Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.exit(-1);
    }

    WikiLanguage lang = WikiLanguage.EN;
    if (args.lang.equalsIgnoreCase("de")) {
      lang = WikiLanguage.DE;
    } else  if (args.lang.equalsIgnoreCase("zh")) {
      lang = WikiLanguage.ZH;
    }

    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(lang).build();

    WikipediaArticlesDump wikipedia = new WikipediaArticlesDump(args.input);

    AtomicInteger cnt = new AtomicInteger();
    wikipedia.stream()
        // See https://en.wikipedia.org/wiki/Wikipedia:Namespace
        .filter(s -> !s.contains("<ns>") || s.contains("<ns>0</ns>"))
        .forEach(s -> {
          out.println("Title = " + cleaner.getTitle(s));
          out.println("Id = " + cleaner.getId(s));
          out.println(cleaner.clean(s) + "\n\n#################################\n");
          cnt.incrementAndGet();
        });

    out.println("Total of " + cnt + " articles read.");
    out.close();
  }
}
