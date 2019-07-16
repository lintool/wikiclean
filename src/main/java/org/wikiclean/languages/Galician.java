package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Galician extends Language {
  public Galician() {
    super("gl");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Notas", "Véxase tamén", "Véxa tamén", "Ligazóns externas");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categoría");
  }
}
