package org.wikiclean;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class WikiCleaner {

  public static final String clean(String page) {
    String content = getWikiMarkup(page);

    content = removeRefs(content);
    content = removeInterWikiLinks(content);
    content = removeImageCaptions(content);
    content = removeHtmlComments(content);
    content = removeDoubleBraces(content);
    content = removeEmphasis(content);
    content = removeFooter(content);
    content = removeHeadings(content);
    content = removeCategoryLinks(content);
    content = removeLinks(content);
    content = removeEmptyParentheticals(content);

    // For some reason, some HTML entities are doubly encoded.
    content = StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeHtml4(content));
    content = compressMultipleNewlines(content);

    return content.trim();
  }

  private static final Pattern EMPTY_PARENS = Pattern.compile(" \\(\\)");

  private static String removeEmptyParentheticals(String s) {
    // Take care of things like: id 36
    // '''Albedo''' ({{IPAc-en|icon|æ|l|ˈ|b|iː|d|oʊ}}), or ''reflection coefficient'' ...
    return EMPTY_PARENS.matcher(s).replaceAll("");
  }
  
  private static final Pattern MULTIPLE_NEWLINES = Pattern.compile("[\\n\\r][\\n\\r]+");

  private static String compressMultipleNewlines(String s) {
    return MULTIPLE_NEWLINES.matcher(s).replaceAll("\n\n");
  }

  private static final Pattern SEE_ALSO = Pattern.compile("==\\s*See also\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static final Pattern REFERENCES = Pattern.compile("==\\s*References\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static final Pattern FURTHER_READING = Pattern.compile("==\\s*Further reading\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static final Pattern EXTERNAL_LINKS = Pattern.compile("==\\s*External Links\\s*==.*",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static String removeFooter(String s) {
    s = SEE_ALSO.matcher(s).replaceAll("");
    s = REFERENCES.matcher(s).replaceAll("");
    s = FURTHER_READING.matcher(s).replaceAll("");
    s = EXTERNAL_LINKS.matcher(s).replaceAll("");

    return s;
  }

  private static final Pattern CATEGORY_LINKS = Pattern.compile("\\[\\[Category:([^\\]]+)\\]\\]");

  private static String removeCategoryLinks(String s) {
    return CATEGORY_LINKS.matcher(s).replaceAll("");
  }

  private static final Pattern LINKS1 = Pattern.compile("\\[\\[[^\\]]+\\|([^\\]]+)\\]\\]");
  private static final Pattern LINKS2 = Pattern.compile("(\\[\\[|\\]\\])");

  private static String removeLinks(String s) {
    return LINKS2.matcher(LINKS1.matcher(s).replaceAll("$1")).replaceAll("");
  }

  private static final Pattern HEADINGS = Pattern.compile("=+\\s*([^=]+)\\s*=+");

  private static String removeHeadings(String s) {
    return HEADINGS.matcher(s).replaceAll("$1");
  }

  private static final Pattern EMPHASIS = Pattern.compile("('''|'')");

  private static String removeEmphasis(String s) {
    return EMPHASIS.matcher(s).replaceAll("");
  }

  private static final Pattern HTML_COMMENT = Pattern.compile("(<|&lt;|&#60;)!--.*?--(>|&gt;|&#62;)",
      Pattern.DOTALL);

  private static String removeHtmlComments(String s) {
    return HTML_COMMENT.matcher(s).replaceAll("");
  }

  private static final int DEFAULT_NO_BRACE = 0;
  private static final int STATE_1CLOSE_BRACE = 1;
  private static final int STATE_1OPEN_BRACE = 2;
  
  public static String removeDoubleBraces(String s) {
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

      s = s.substring(0, i) + s.substring(cur+1, s.length());
      i = s.indexOf("{{", i);
    }

    return s;
  }

  private static final int DEFAULT_NO_BRACKET = 0;
  private static final int STATE_1CLOSE_BRACKET = 1;
  private static final int STATE_1OPEN_BRACKET = 2;
  
  public static String removeImageCaptions(String s) {
    String[] labels = { "[[File:", "[[Image:" };
    for ( String label : labels) {
      s = removeImageCaptionsLabel(s, label);
    }
    return s;
  }

  public static String removeImageCaptionsLabel(String s, String label) {
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
        if (state == STATE_1OPEN_BRACKET ) {
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

      s = s.substring(0, i) + s.substring(cur+1, s.length());
      i = s.indexOf(label, i);
    }

    return s;
  }

  private static final Pattern REF1 = Pattern.compile("&lt;ref[^/]+/&gt;", Pattern.DOTALL);
  private static final Pattern REF2 = Pattern.compile("&lt;ref.*?&lt;/ref&gt;", Pattern.DOTALL);

  private static String removeRefs(String s) {
    s = REF1.matcher(s).replaceAll("");
    s = REF2.matcher(s).replaceAll("");
    return s;
  }

  // Note that WiktionaryLinks have the form [[wikt:anarchism|anarchism]], which is easily confused with
  // inter-wikilinks. The distinguishing characteristic is the lack of pipe (|).
  private static final Pattern INTER_WIKI_LINKS = Pattern.compile("\\[\\[[a-z\\-]+:[^|\\]]+\\]\\]");

  private static String removeInterWikiLinks(String s) {
    return INTER_WIKI_LINKS.matcher(s).replaceAll(" ");
  }

  /**
   * Start delimiter of the text, which is &lt;<code>text xml:space="preserve"</code>&gt;. 
   * Note: No close bracket because text element can have multiple attributes.
   */
  protected static final String XML_START_TAG_TEXT = "<text xml:space=\"preserve\"";

  /**
   * End delimiter of the text, which is &lt;<code>/text</code>&gt;.
   */
  protected static final String XML_END_TAG_TEXT = "</text>";

  public static String getWikiMarkup(String s) {
    // parse out actual text of article
    int textStart = s.indexOf(XML_START_TAG_TEXT);
    int textEnd = s.indexOf(XML_END_TAG_TEXT, textStart);

    if (textStart == -1 || textStart + 27 > textEnd) {
      // Returning empty string is preferable to returning null to prevent NPE.
      return "";
    }

    return s.substring(textStart + 27, textEnd);
  }
}
