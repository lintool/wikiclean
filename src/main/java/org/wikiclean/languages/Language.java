package org.wikiclean.languages;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Base class with some common logic for language-specific cleanup of text
 * Subclasses can just provide patterns to be cleaned or define their own logic
 */
public abstract class Language {
  private final String code;

  /**
   * @param code ISO 639-1 language code
   */
  protected Language(String code) {
    this.code = code;
  }

  private String cleanWithPatterns(String text, List<Pattern> patterns) {
    String cleaned = text;

    for (Pattern pattern : patterns) {
      cleaned = pattern.matcher(cleaned).replaceAll("");
    }
    return cleaned;
  }

  /**
   * Patterns used in removeFooter's default implementation
   * @return list of patterns to delete from footers
   */
  protected abstract List<Pattern> footerPatterns();

  /**
   * built common patterns out of headlines
   * @param headings headings to use in patterns
   * @return patterns for footerPatterns()
   * */
  protected List<Pattern> footerPatterns(String... headings)  {
    List<Pattern> patterns = new LinkedList<>();
    for (String heading : headings) {
      patterns.add(Pattern.compile("==\\s*" + heading + "\\s*==.*",
          Pattern.CASE_INSENSITIVE | Pattern.DOTALL));
    }
    return patterns;
  }

  /**
   * Patterns used in removeCategoryLink's default implementation
   * @return list of patterns to delete
   */
  protected abstract List<Pattern> categoryLinkPatterns();

  /**
   * built common patterns out of category names
   * @param names names to use in patterns
   * @return patterns for categoryLinkPatterns()
   * */
  protected List<Pattern> categoryLinkPatterns(String... names) {
    List<Pattern> patterns = new LinkedList<>();
    for (String name : names) {
      patterns.add(Pattern.compile("\\[\\[" + name + ":([^\\]]+)\\]\\]"));

    }
    return patterns;
  }

  /**
   * used to clean footers in {@link org.wikiclean.WikiClean}
   * @param footer footer to clean
   * @return cleaned footer
   */
  public String removeFooter(String footer) {
    return cleanWithPatterns(footer, footerPatterns());
  }

  public String removeCategoryLinks(String text) {
    return cleanWithPatterns(text, categoryLinkPatterns());
  }

  /**
   * @return ISO 639-1 language code for this language
   */
  public String getCode() {
    return code;
  }
}
