package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Catalan extends Language {
  public Catalan() {
    super("ca");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Enllaços externs", "Referències", "Vegeu també", "Notes");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categoria");
  }
}
