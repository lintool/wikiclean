package org.wikiclean.languages;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class English extends Language {

  private static final Pattern FOOTER1 = Pattern.compile("==\\s*See also\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER2 = Pattern.compile("==\\s*References\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER3 = Pattern.compile("==\\s*Further reading\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER4 = Pattern.compile("==\\s*External Links\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER5 = Pattern.compile("==\\s*Related pages\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  // public since it's reused in some languages
  public static final String CATEGORY_LINKS1 = "Category";

  public English() {
    super("en");
  }


  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("See also", "References", "Further reading", "External Links", "Related pages");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns(CATEGORY_LINKS1);
  }
}
