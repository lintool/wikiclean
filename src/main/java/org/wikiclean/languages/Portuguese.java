package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Portuguese extends Language {
  public Portuguese() {
    super("pt");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Ver também", "Notas", "Referências", "Ligações externas");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Categoria");
  }
}
