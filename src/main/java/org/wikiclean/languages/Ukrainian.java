package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Ukrainian extends Language {
  public Ukrainian() {
    super("uk");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Посилання", "Див. також", "Примітки", "Джерела");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Категорія");
  }
}
