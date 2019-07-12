package org.wikiclean.languages;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Kannada extends Language {

  public Kannada() {
    super("kn");
  }

  private static final Pattern FOOTER1 = Pattern.compile("==\\s*ಉಲ್ಲೇಖ(ಗಳು)?+\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER2 = Pattern.compile("==\\s*(ಹೊರ(ಗಿನ)?+|ಬಾಹ್ಯ)\\s+(ಕೊಂಡಿ|ಸಂಪರ್ಕ)(ಗಳು)?+\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER3 = Pattern.compile("==\\s*ಆಕರಗಳು\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
  private static final Pattern FOOTER4 = Pattern.compile("==\\s*(ಇದನ್ನೂ|ಇವನ್ನೂ|ಕೆಳಗಿನ\\s+ಲೇಖನಗಳನ್ನೂ)?+\\s*ನೋಡಿ\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  @Override
  protected List<Pattern> footerPatterns() {
    return Arrays.asList(FOOTER1, FOOTER2, FOOTER3, FOOTER4);
  }

  private static final String CATEGORY_LINKS1 = "ವರ್ಗ";

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns(English.CATEGORY_LINKS1, CATEGORY_LINKS1); // Some pages of KN use same category tag as
  }

}
