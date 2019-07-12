/**
 * WikiClean: A Java Wikipedia markup to plain text converter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wikiclean;

import org.apache.commons.lang3.StringEscapeUtils;
import org.wikiclean.languages.English;
import org.wikiclean.languages.Language;
import org.wikiclean.languages.Languages;

import java.util.regex.Pattern;

/**
 * Main WikiClean class for converting Wikipedia articles to plain text.
 */
public class WikiClean {
  /**
   * Enumeration of supported Wikipedia languages.
   * Name of enum is assumed to be uppercase version of ISO 639-1 code used in {@code Language.getCode()}
   * @deprecated use {@link Language} objects
   */
  @Deprecated
  public enum WikiLanguage {
    /** English */
    EN,
    /** German */
    DE,
    /** Chinese */
    ZH,
    /** Kannada */
    KN
  };

  private boolean withTitle;
  private boolean withFooter;
  private Language lang;

  private boolean keepLinks = false;

  // Use the builder to construct.
  private WikiClean() {
  }

  private void setKeepLinks(boolean flag) {
    this.keepLinks = flag;
  }

  /**
   * Asks this cleaner whether or not links are cleaned.
   * @return whether or not links are cleaned
   */
  public boolean keepLinks() {
    return keepLinks;
  }

  /**
   * Asks this cleaner whether or not links are cleaned.
   * @return whether or not links are cleaned
   * @deprecated typo, use keepLinks
   */
  @Deprecated
  public boolean keepLings() {
    return keepLinks();
  }

  private void setWithTitle(boolean flag) {
    this.withTitle = flag;
  }

  /**
   * Asks this cleaner whether or not title is included in the output.
   * @return whether or not title is included in the output
   */
  public boolean withTitle() {
    return withTitle;
  }

  private void setWithFooter(boolean flag) {
    this.withFooter = flag;
  }

  /**
   * Asks this cleaner whether or not footer is included in the output.
   * @return whether or not footer is included in the output
   */
  public boolean withFooter() {
    return withFooter;
  }

  private void setLanguage(Language lang) {
    this.lang = lang;
  }
  /**
   * Asks this cleaner what language it is expecting.
   * @return language expected
   * @deprecated use getLanguage()
   */
  @Deprecated
  public WikiLanguage language() {
    return WikiLanguage.valueOf(lang.getCode().toUpperCase());
  }

  public Language getLanguage() {
    return lang;
  }

  private static final String XML_START_TAG_TITLE = "<title>";
  private static final String XML_END_TAG_TITLE = "</title>";

  /**
   * Returns the title of a Wikipedia article
   * @param s Wikipedia article
   * @return article title
   */
  public final String getTitle(String s) {
    int start = s.indexOf(XML_START_TAG_TITLE);
    int end = s.indexOf(XML_END_TAG_TITLE, start);
    if (start < 0 || end < 0) {
      return "";
    }
    return StringEscapeUtils.unescapeHtml4(s.substring(start + 7, end));
  }

  private static final String XML_START_TAG_ID = "<id>";
  private static final String XML_END_TAG_ID = "</id>";

  /**
   * Returns the id of a Wikipedia article
   * @param s Wikipedia article
   * @return article id
   */
  public final String getId(String s) {
    // parse out the document id
    int start = s.indexOf(XML_START_TAG_ID);
    int end = s.indexOf(XML_END_TAG_ID);
    return (start == -1 || end == -1 || start > end) ? "0" : s.substring(start + 4, end);
  }

  private static final String XML_START_TAG_TEXT = "<text xml:space=\"preserve\"";
  private static final String XML_END_TAG_TEXT = "</text>";

  /**
   * Returns the Wikipedia markup of a Wikipedia article
   * @param s Wikipedia article
   * @return Wikipedia markup
   */
  public String getWikiMarkup(String s) {
    // parse out actual text of article
    int textStart = s.indexOf(XML_START_TAG_TEXT);
    int textEnd = s.indexOf(XML_END_TAG_TEXT, textStart);

    if (textStart == -1 || textStart + 27 > textEnd) {
      // Returning empty string is preferable to returning null to prevent NPE.
      return "";
    }

    String s2 = s.substring(textStart + 27, textEnd);
    if (s2.startsWith("bytes=")) {
      textEnd = s2.indexOf(">");
      s2 = s2.substring(textEnd + 1);
    }

    return s2;
  }

  /**
   * Cleans a Wikipedia article.
   * @param page Wikipedia article
   * @return cleaned output
   */
  public String clean(String page) {
    String content = getWikiMarkup(page);

    if (!withFooter) {
      content = removeFooter(content);
    }

    content = removeRefs(content);
    content = removeInterWikiLinks(content);
    content = removeParentheticals(content);
    content = fixUnitConversion(content);
    content = ImageCaptionsRemover.remove(content);
    content = DoubleBracesRemover.remove(content);
    content = removeHtmlComments(content);
    content = removeEmphasis(content);
    content = removeHeadings(content);
    content = removeCategoryLinks(content);

    if (!keepLinks) {
      content = removeLinks(content);
    }

    content = removeMath(content);
    content = removeGallery(content);
    content = removeNoToc(content);
    content = removeIndentation(content);

    content = TableRemover.remove(content);

    // For some reason, some HTML entities are doubly encoded.
    content = StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeHtml4(content));
    content = removeHtmlTags(content);

    // Finally, fold multiple newlines.
    content = compressMultipleNewlines(content);

    if (withTitle) {
      return getTitle(page) + "\n\n" + content.trim();
    }

    return content.trim();
  }

  private static final Pattern UNIT_CONVERSION1 =
      Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\}\\}");
  private static final Pattern UNIT_CONVERSION2 =
      Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\|[^}]+\\}\\}");

  private String fixUnitConversion(String s) {
    String t = UNIT_CONVERSION1.matcher(s).replaceAll("$1 $2");
    return UNIT_CONVERSION2.matcher(t).replaceAll("$1 $2");
  }

  private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");

  private String removeHtmlTags(String s) {
    return HTML_TAGS.matcher(s).replaceAll("");
  }

  private static final Pattern GALLERY = Pattern.compile("&lt;gallery&gt;.*?&lt;/gallery&gt;",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private String removeGallery(String s) {
    return GALLERY.matcher(s).replaceAll("");
  }

  private static final Pattern NO_TOC = Pattern.compile("__NOTOC__");

  private String removeNoToc(String s) {
    return NO_TOC.matcher(s).replaceAll("");
  }

  private static final Pattern INDENTATION = Pattern.compile("[\\n\\r]:\\s*");

  private String removeIndentation(String s) {
    return INDENTATION.matcher(s).replaceAll("\n");
  }

  private static final Pattern MATH = Pattern.compile("&lt;math&gt;.*?&lt;/math&gt;",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private String removeMath(String s) {
    return MATH.matcher(s).replaceAll("");
  }

  // IPA parenthetical may be enclosed either with parentheses or brackets (de articles).
  private static final Pattern IPA1 = Pattern.compile(" (\\(|\\[)\\{\\{IPA[^\\}]+\\}\\}(\\)|\\])");
  private static final Pattern IPA2 = Pattern.compile(" \\{\\{IPA[^\\}]+\\}\\}");

  private String removeParentheticals(String s) {
    // Take care of things like: id 36
    // '''Albedo''' ({{IPAc-en|icon|æ|l|ˈ|b|iː|d|oʊ}}), or ''reflection coefficient'' ...
    //
    // Note that we shouldn't just leave to the double-curly remover, since that would leave
    // the dangling empty parens.
    s = IPA1.matcher(s).replaceAll("");

    // Straight-up IPA, with no parenthetical.
    s = IPA2.matcher(s).replaceAll("");

    return s;
  }

  private static final Pattern MULTIPLE_NEWLINES = Pattern.compile("[\\n\\r][\\n\\r]+");

  private String compressMultipleNewlines(String s) {
    return MULTIPLE_NEWLINES.matcher(s).replaceAll("\n\n");
  }

  private String removeFooter(String s) {
    return lang.removeFooter(s);
  }

  private String removeCategoryLinks(String s) {
    return lang.removeCategoryLinks(s);
  }

  private static final Pattern LINKS1 = Pattern.compile("\\[\\[[^\\]]+\\|([^\\]]+)\\]\\]");
  private static final Pattern LINKS2 = Pattern.compile("(\\[\\[|\\]\\])");

  private String removeLinks(String s) {
    return LINKS2.matcher(LINKS1.matcher(s).replaceAll("$1")).replaceAll("");
  }

  private static final Pattern HEADINGS = Pattern.compile("=+\\s?(.*?)=+");

  private String removeHeadings(String s) {
    // Make sure there's an extra newline after headings.
    return HEADINGS.matcher(s).replaceAll("$1\n");
  }

  private static final Pattern EMPHASIS = Pattern.compile("('''|'')");

  private String removeEmphasis(String s) {
    return EMPHASIS.matcher(s).replaceAll("");
  }

  private static final Pattern HTML_COMMENT = Pattern.compile(
      "(<|&lt;|&#60;)!--.*?--(>|&gt;|&#62;)", Pattern.DOTALL);

  private String removeHtmlComments(String s) {
    return HTML_COMMENT.matcher(s).replaceAll("");
  }

  private static final Pattern BR = Pattern.compile("&lt;br */&gt;");
  private static final Pattern REF1 = Pattern.compile("&lt;ref[^/]+/&gt;", Pattern.DOTALL);
  private static final Pattern REF2 = Pattern.compile("&lt;ref.*?&lt;/ref&gt;", Pattern.DOTALL);

  private String removeRefs(String s) {
    s = BR.matcher(s).replaceAll(""); // See test case for why we do this.
    s = REF1.matcher(s).replaceAll("");
    s = REF2.matcher(s).replaceAll("");
    return s;
  }

  // Note that WiktionaryLinks have the form [[wikt:anarchism|anarchism]], which is easily confused
  // with
  // inter-wikilinks. The distinguishing characteristic is the lack of pipe (|).
  private static final Pattern INTER_WIKI_LINKS = Pattern.compile("\\[\\[[a-z\\-]+:[^|\\]]+\\]\\]");

  private String removeInterWikiLinks(String s) {
    return INTER_WIKI_LINKS.matcher(s).replaceAll(" ");
  }

  private static final class ImageCaptionsRemover {
    private static final int DEFAULT_NO_BRACKET = 0;
    private static final int STATE_1CLOSE_BRACKET = 1;
    private static final int STATE_1OPEN_BRACKET = 2;

    private static String remove(String s) {
      String[] labels = { "[[File:", "[[Image:", "[[Datei" // We see this in de wikipedia.
      };
      for (String label : labels) {
        s = removeLabel(s, label);
      }
      return s;
    }

    // This method encodes a finite state machine to handle links in caption, which result in
    // nested [[ ... [[foo]] ... ]] constructs.
    private static String removeLabel(String s, String label) {
      int i = s.indexOf(label);
      while (i != -1) {
        int state = DEFAULT_NO_BRACKET;
        int level = 1;
        int cur = i + label.length();

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACKET && s.charAt(cur) == '[') {
            level++;
            state = DEFAULT_NO_BRACKET;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACKET) {
            state = DEFAULT_NO_BRACKET;
          }
          if (s.charAt(cur) == '[') {
            state = STATE_1OPEN_BRACKET;
          }

          if (state == STATE_1CLOSE_BRACKET && s.charAt(cur) == ']') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT_NO_BRACKET;
          } else {
            // If there's only one close, move back to default state.
            if (state == STATE_1CLOSE_BRACKET) {
              state = DEFAULT_NO_BRACKET;
            }
            if (s.charAt(cur) == ']') {
              state = STATE_1CLOSE_BRACKET;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf(label, i);
      }

      return s;
    }
  }

  private static final class DoubleBracesRemover {
    private static final int DEFAULT_NO_BRACE = 0;
    private static final int STATE_1CLOSE_BRACE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    // This method encodes a finite state machine to handle nested double braces (e.g., in
    // infoboxes).
    private static String remove(String s) {
      int i = s.indexOf("{{");
      while (i != -1) {
        int state = DEFAULT_NO_BRACE;
        int level = 1;
        int cur = i + 2;

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACE && s.charAt(cur) == '{') {
            level++;
            state = DEFAULT_NO_BRACE;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACE) {
            state = DEFAULT_NO_BRACE;
          }
          if (s.charAt(cur) == '{') {
            state = STATE_1OPEN_BRACE;
          }

          if (state == STATE_1CLOSE_BRACE && s.charAt(cur) == '}') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT_NO_BRACE;
          } else {
            // If there's only one close, move back to default state.
            if (state == STATE_1CLOSE_BRACE) {
              state = DEFAULT_NO_BRACE;
            }
            if (s.charAt(cur) == '}') {
              state = STATE_1CLOSE_BRACE;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf("{{", i);
      }

      return s;
    }
  }

  private static final class TableRemover {
    private static final int DEFAULT = 0;
    private static final int STATE_PIPE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    private static String remove(String s) {
      int i = s.indexOf("{|");
      while (i != -1) {
        int state = DEFAULT;
        int level = 1;
        int cur = i + 2;

        while (cur < s.length()) {
          if (state == STATE_1OPEN_BRACE && s.charAt(cur) == '|') {
            level++;
            state = DEFAULT;
          }
          // If there's only one close, move back to default state.
          if (state == STATE_1OPEN_BRACE) {
            state = DEFAULT;
          }
          if (s.charAt(cur) == '{') {
            state = STATE_1OPEN_BRACE;
          }

          if (state == STATE_PIPE && s.charAt(cur) == '}') {
            level--;
            if (level == 0) {
              break;
            }
            state = DEFAULT;
          } else {
            // If there's a pipe but no close brace, move back to default state.
            if (state == STATE_PIPE) {
              state = DEFAULT;
            }
            if (s.charAt(cur) == '|') {
              state = STATE_PIPE;
            }
          }
          cur++;
        }

        if (cur == s.length()) {
          return s.substring(0, i);
        }

        s = s.substring(0, i) + s.substring(cur + 1, s.length());
        i = s.indexOf("{|", i);
      }

      return s;
    }
  }

  /**
   * Builder object for {@link WikiClean}.
   */
  public static class Builder {
    private boolean withTitle = false;
    private boolean withFooter = false;
    private boolean keepLinks = false;
    private Language lang = new English();

    /**
     * Class constructor.
     */
    public Builder() {}

    /**
     * Sets whether or not to keep the title.
     * @param flag whether or not to keep the title
     * @return self for method chaining
     */
    public Builder withTitle(boolean flag) {
      this.withTitle = flag;
      return this;
    }

    /**
     * Sets whether or not to keep the footer.
     * @param flag whether or not to keep the footer
     * @return self for method chaining
     */
    public Builder withFooter(boolean flag) {
      this.withFooter = flag;
      return this;
    }

    /**
     * Sets the language.
     * @param lang language
     * @return self for method chaining
     * @deprecated use method acepting a {@link Language}
     */
    @Deprecated
    public Builder withLanguage(WikiLanguage lang) {
      this.lang = Languages.language(lang.name().toLowerCase()).get();
      return this;
    }

    /**
     * Sets the language.
     * @param lang language, e.g. from {@link Languages}
     * @return self for method chaining
     */
    public Builder withLanguage(Language lang) {
      this.lang = lang;
      return this;
    }

    /**
     * Keeps the links.
     * @return self for method chaining
     */
    public Builder keepLinks() {
      this.keepLinks = true;
      return this;
    }

    /**
     * Constructs the {@link WikiClean} instance.
     * @return the {@link WikiClean} instance
     */
    public WikiClean build() {
      WikiClean clean = new WikiClean();
      clean.setWithTitle(withTitle);
      clean.setWithFooter(withFooter);
      clean.setLanguage(lang);
      clean.setKeepLinks(keepLinks);

      return clean;
    }
  }
}
