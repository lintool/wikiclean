package org.wikiclean.languages;

import java.util.List;
import java.util.regex.Pattern;

public class Greek extends Language {
  public Greek() {
    super("el");
  }

  @Override
  protected List<Pattern> footerPatterns() {
    return footerPatterns("Παραπομπές", "Δείτε επίσης", "Κατηγορίες", "Πηγές", "Εξωτερικοί σύνδεσμοι");
  }

  @Override
  protected List<Pattern> categoryLinkPatterns() {
    return categoryLinkPatterns("Κατηγορία");
  }
}
