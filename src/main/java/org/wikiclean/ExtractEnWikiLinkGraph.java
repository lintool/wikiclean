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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtractEnWikiLinkGraph {
  private static final class Args {
    @Option(name = "-input", metaVar = "[path]", required = true, usage = "input path")
    File input;

    @Option(name = "-titles", metaVar = "[path]", required = true, usage = "article titles")
    File titles;

    @Option(name = "-output", metaVar = "[path]", required = true, usage = "output path")
    String output;
  }

  private static final Pattern LINKS1 = Pattern.compile("\\[\\[([^\\]]+)\\|([^\\]]+)\\]\\]");
  private static final Pattern LINKS2 = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");

  private static Set<Integer> extractLinks(String s, Map<String, Integer> titles) {
    s = LINKS1.matcher(s).replaceAll("[[$1]]");

    Set<String> links2 = new TreeSet<>();
    Matcher m = LINKS2.matcher(s);
    while (m.find()) {
      links2.add(m.group(1));
    }

    final Set<Integer> ids = new TreeSet<>();
    links2.forEach(target -> {
      if (titles.containsKey(target)) {
        ids.add(titles.get(target));
      } else {
        // https://en.wikipedia.org/wiki/cloud redirects automatically to
        // https://en.wikipedia.org/wiki/Cloud
        String initialCaps = target.substring(0, 1).toUpperCase() + target.substring(1);
        if (titles.containsKey(initialCaps)) {
          ids.add(titles.get(initialCaps));
        } else if (target.contains("_")) {
          // Sometimes spaces are replaced with underscores.
          String underscoresRemoved = target.replaceAll("_", " ");
          if (titles.containsKey(underscoresRemoved)) {
            ids.add(titles.get(underscoresRemoved));
          } else {
            System.out.println(String.format(" - warning: target not found for '%s'", target));
          }
        } else {
          System.out.println(String.format(" - warning: target not found for '%s'", target));
        }
      }
    });

    return ids;
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

    final Map<String, Integer> titles = new HashMap<>();
    BufferedReader reader = new BufferedReader(new FileReader(args.titles));
    String line = reader.readLine();
    while (line != null) {
      String arr[] = line.split("\\t");
      titles.put(arr[1], Integer.parseInt(arr[0]));
      line = reader.readLine();
    }
    reader.close();
    System.out.println("Number of article titles loaded: " + titles.size());

    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    PrintWriter writer = new PrintWriter(args.output, "UTF-8");

    WikipediaArticlesDump wikipedia = new WikipediaArticlesDump(args.input);
    WikiClean cleaner = new WikiClean.Builder().keepLinks().build();

    AtomicInteger vertices = new AtomicInteger();
    AtomicInteger edges = new AtomicInteger();

    wikipedia.stream()
        // See https://en.wikipedia.org/wiki/Wikipedia:Namespace
        .filter(s -> !s.contains("<ns>") || s.contains("<ns>0</ns>"))
        .forEach(s -> {
          String title = cleaner.getTitle(s);
          String id = cleaner.getId(s);
          out.println(String.format("# Processing article '%s', id = %s", title, id));
          Set<Integer> ids = extractLinks(cleaner.clean(s), titles);
          writer.println(id + "\t" +
              ids.stream().map(n -> n.toString()).collect(Collectors.joining("\t")));
          vertices.incrementAndGet();
          edges.getAndAdd(ids.size());
        });

    writer.close();
    out.println(String.format("Size of graph: %d vertices, %d edges", vertices, edges));
    out.close();
  }
}
