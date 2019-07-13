package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class German extends Language {

  public German() {
    super("de");
  }


  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Referenzen", "Weblinks", "Literatur", "Einzelnachweise", "Siehe auch", "Quellen");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Kategorie");
  }
}
