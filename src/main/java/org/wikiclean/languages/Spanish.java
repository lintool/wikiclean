package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Spanish extends Language {
  public Spanish() {
    super("es");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Enlaces externos", "Referencias", "Véase también", "Notas");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categoría");
  }
}
