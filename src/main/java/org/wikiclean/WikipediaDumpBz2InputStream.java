package org.wikiclean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.tools.bzip2.CBZip2InputStream;

public class WikipediaDumpBz2InputStream {
  private static int DEFAULT_STRINGBUFFER_CAPACITY = 1024;

  private BufferedReader br;
  private FileInputStream fis;

  /**
   * Creates an input stream for reading Wikipedia articles from a bz2-compressed dump file.
   *
   * @param file path to dump file
   * @throws IOException
   */
  public WikipediaDumpBz2InputStream(String file) throws IOException {
    br = null;
    fis = new FileInputStream(file);
    byte[] ignoreBytes = new byte[2];
    fis.read(ignoreBytes); // "B", "Z" bytes from commandline tools
    br = new BufferedReader(new InputStreamReader(new CBZip2InputStream(fis), "UTF8"));
  }

  public String readNext() throws IOException {
    String s = null;
    StringBuffer sb = new StringBuffer(DEFAULT_STRINGBUFFER_CAPACITY);

    while ((s = br.readLine()) != null) {
      if (s.endsWith("<page>"))
        break;
    }

    if (s == null) {
      fis.close();
      br.close();
      return null;
    }

    sb.append(s + "\n");

    while ((s = br.readLine()) != null) {
      sb.append(s + "\n");

      if (s.endsWith("</page>"))
        break;
    }

    return sb.toString();
  }

  private static final String INPUT_OPTION = "input";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("gzipped XML dump file").create(INPUT_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INPUT_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(WikipediaDumpBz2InputStream.class.getCanonicalName(), options);
      System.exit(-1);
    }

    String path = cmdline.getOptionValue(INPUT_OPTION);
    PrintStream out = new PrintStream(System.out, true, "UTF-8");

    WikipediaDumpBz2InputStream stream = new WikipediaDumpBz2InputStream(path);
    String page;
    while ((page = stream.readNext()) != null) {
      out.println("Title = " + WikiCleaner.getTitle(page));
      out.println("Id = " + WikiCleaner.getId(page));
      out.println(WikiCleaner.clean(page) + "\n\n#################################\n");
    }
    out.close();
  }
}
