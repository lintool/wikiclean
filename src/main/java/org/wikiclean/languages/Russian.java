package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Russian extends Language {
  public Russian() {
    super("ru");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Примечания", "Ссылки", "Литература", "См. также");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Категория");
  }
}
