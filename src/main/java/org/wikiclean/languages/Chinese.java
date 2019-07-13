package org.wikiclean.languages;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Chinese extends Language {

  public Chinese() {
    super("zh");
  }

  private static final Pattern FOOTER1 = Pattern.compile("==\\s*参见\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER2 = Pattern.compile("==\\s*参考书目\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER3 = Pattern.compile("==\\s*参考网址\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  @Override
  protected List<Pattern> footerPatterns() {
    return Arrays.asList(FOOTER1, FOOTER2, FOOTER3);
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns(English.CATEGORY_LINKS1); //ZH use the same category tag as EN
  }
}
