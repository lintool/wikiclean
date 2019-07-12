package org.wikiclean.languages;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class German extends Language {

  public German() {
    super("de");
  }

  private static final Pattern FOOTER1 = Pattern.compile("==\\s*Referenzen\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER2 = Pattern.compile("==\\s*Weblinks\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER3 = Pattern.compile("==\\s*Literatur\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER4 = Pattern.compile("==\\s*Einzelnachweise\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER5 = Pattern.compile("==\\s*Siehe auch\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER6 = Pattern.compile("==\\s*Quellen\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);


  @Override
  protected List<Pattern> footerPatterns() {
    return Arrays.asList(FOOTER1, FOOTER2, FOOTER3, FOOTER4, FOOTER5, FOOTER6);
  }

  private static final Pattern CATEGORY_LINKS1 = Pattern
          .compile("\\[\\[Kategorie:([^\\]]+)\\]\\]");

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return Arrays.asList(CATEGORY_LINKS1);
  }
}
