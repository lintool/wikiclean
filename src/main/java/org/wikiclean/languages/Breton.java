package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Breton extends Language {
  public Breton() {
    super("br");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns( "Notennoù", "Liamm diavaez", "Notennoù ha daveennoù", "Levrlennadur", "Daveoù", "Roll ar rummadoù");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Rummad");
  }
}
