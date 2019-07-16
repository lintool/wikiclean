package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Italian extends Language {
  public Italian() {
    super("it");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Note", "Voci correlate", "Altri progetti", "Collegamenti esterni");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categoria");
  }
}
