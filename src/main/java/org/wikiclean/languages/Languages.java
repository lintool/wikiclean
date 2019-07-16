package org.wikiclean.languages;

import java.util.*;

/**
 * Manages available languages for WikiClean
 */
public final class Languages {
  private Languages() {
  }

  private static final Map<String, Language> langs = new HashMap<>();

  static {
    // add built-in languages
    addLanguage(new English());
    addLanguage(new German());
    addLanguage(new Chinese());
    addLanguage(new Kannada());
    addLanguage(new French());
  }

  /**
   * @return all available languages (immutable)
   */
  public static Collection<Language> languages() {
    return Collections.unmodifiableCollection(langs.values());
  }

  /**
   * Add a language to the list
   *
   * @param lang language to add
   * @return true if language was added, false if language with given code already existed
   */
  public static boolean addLanguage(Language lang) {
    if (languages().stream().anyMatch(language -> lang.getCode().equals(language.getCode()))) {
      return false;
    }
    langs.put(lang.getCode(), lang);
    return true;
  }

  /**
   * @param code code of language to fetch
   * @return language with given code if present, else empty optional
   */
  public static Optional<Language> language(String code) {
    Language lang = langs.get(code);
    return Optional.ofNullable(lang);
  }
}

