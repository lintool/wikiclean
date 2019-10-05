package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Dutch extends Language {
  public Dutch() {
    super("nl");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Externe links", "Secundaire literatuur", "Noten", "Bronnen, noten en/of referenties", "Zie ook");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categorie");
  }
}
