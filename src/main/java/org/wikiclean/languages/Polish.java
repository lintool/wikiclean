package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Polish extends Language {
  public Polish() {
    super("pl");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Przypisy", "Linki zewnętrzne", "Uwagi", "Zobacz też");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Kategoria");
  }
}
