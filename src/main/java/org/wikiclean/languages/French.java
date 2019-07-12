package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class French extends Language {
  public French() {
    super("fr");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Références", "Voir aussi", "Sources", "Annexes", "Articles connexes", "Notes et références", "Liens externes", "Bibliographie", "Source de la traduction", "Source");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Catégorie");
  }
}
