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

public class ExtractLinkGraph {
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

  public static Set<Integer> extractLinks(String s, Map<String, Integer> titles) {
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
        //System.out.println(target + " " + titles.get(target));
      } else {
        String initialCaps = target.substring(0, 1).toUpperCase() + target.substring(1);
        if (titles.containsKey(initialCaps)) {
          ids.add(titles.get(initialCaps));
          //System.out.println(target + " " + titles.get(initialCaps));
        } else if (target.contains("_")) {
          String underscoresRemoved = target.replaceAll("_", " ");
          if (titles.containsKey(underscoresRemoved)) {
            ids.add(titles.get(underscoresRemoved));
          } else {
            System.out.println("ERROR! " + target);
          }
        } else {
          System.out.println("ERROR! " + target);
        }
      }
    });
    //System.out.println(String.join("\n", links2));

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
      //System.out.println("#" + arr[0] + "-" + arr[1]);
      titles.put(arr[1], Integer.parseInt(arr[0]));
      line = reader.readLine();
    }
    reader.close();
    System.out.println("Number of article titles: " + titles.size());

    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    PrintWriter writer = new PrintWriter(args.output, "UTF-8");

    WikipediaArticlesDump wikipedia = new WikipediaArticlesDump(args.input);
    WikiClean cleaner = new WikiClean.Builder().keepLinks().build();

    AtomicInteger cnt = new AtomicInteger();
    AtomicInteger links = new AtomicInteger();
    wikipedia.stream()
        // See https://en.wikipedia.org/wiki/Wikipedia:Namespace
        .filter(s -> !s.contains("<ns>") || s.contains("<ns>0</ns>"))
        .forEach(s -> {
          String title = cleaner.getTitle(s);
          String id = cleaner.getId(s);
          out.println("### " + title + " " + id);
          Set<Integer> ids = extractLinks(cleaner.clean(s), titles);
          writer.println(id + "\t" + ids.stream().map(n -> n.toString()).collect(Collectors.joining("\t")));
          cnt.incrementAndGet();
          links.getAndAdd(ids.size());
        });

    writer.close();
    out.println("Total of " + cnt + " nodes, " + links + " edges.");
    out.close();
  }
}
