package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Esperanto extends Language {
  public Esperanto() {
    super("eo");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Eksteraj ligiloj", "Referencoj", "Vidu ankaŭ", "Notoj kaj referencoj", "Notoj", "Fontoj", "Notoj kaj fontoj");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Kategorio");
  }
}
