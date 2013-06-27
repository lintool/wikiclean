/**
 * WikiClean
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

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class WikiClean {
  // Use the builder to construct.
  protected WikiClean() {}

  /**
   * Start delimiter of the title, which is &lt;<code>title</code>&gt;.
   */
  protected static final String XML_START_TAG_TITLE = "<title>";

  /**
   * End delimiter of the title, which is &lt;<code>/title</code>&gt;.
   */
  protected static final String XML_END_TAG_TITLE = "</title>";

  public static final String getTitle(String s) {
    int start = s.indexOf(XML_START_TAG_TITLE);
    int end = s.indexOf(XML_END_TAG_TITLE, start);
    if (start < 0 || end < 0) {
      return "";
    }
    return StringEscapeUtils.unescapeHtml(s.substring(start + 7, end));
  }
  
  /**
   * Start delimiter of the id, which is &lt;<code>id</code>&gt;.
   */
  protected static final String XML_START_TAG_ID = "<id>";

  /**
   * End delimiter of the id, which is &lt;<code>/id</code>&gt;.
   */
  protected static final String XML_END_TAG_ID = "</id>";

  public static final String getId(String s) {
    // parse out the document id
    int start = s.indexOf(XML_START_TAG_ID);
    int end = s.indexOf(XML_END_TAG_ID);
    return (start == -1 || end == -1 || start > end) ? "0" : s.substring(start + 4, end);
  }

  public String clean(String page) {
    String content = getWikiMarkup(page);

    content = removeRefs(content);
    content = removeInterWikiLinks(content);
    content = fixUnitConversion(content);
    content = ImageCaptionsRemover.remove(content);
    content = DoubleBracesRemover.remove(content);
    content = removeHtmlComments(content);
    content = removeEmphasis(content);
    content = removeFooter(content);
    content = removeHeadings(content);
    content = removeCategoryLinks(content);
    content = removeLinks(content);
    content = removeEmptyParentheticals(content);
    content = removeMath(content);
    content = TableRemover.remove(content);

    // For some reason, some HTML entities are doubly encoded.
    content = StringEscapeUtils.unescapeHtml(StringEscapeUtils.unescapeHtml(content));
    content = removeHtmlTags(content);

    // Finally, fold multiple newlines.
    content = compressMultipleNewlines(content);

    return content.trim();
  }

  private static final Pattern UNIT_CONVERSION1 = Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\}\\}");
  private static final Pattern UNIT_CONVERSION2 = Pattern.compile("\\{\\{convert\\|(\\d+)\\|([^|]+)\\|[^}]+\\}\\}");

  private static String fixUnitConversion(String s) {
    String t = UNIT_CONVERSION1.matcher(s).replaceAll("$1 $2");
    return UNIT_CONVERSION2.matcher(t).replaceAll("$1 $2");
  }

  private static final Pattern HTML_TAGS = Pattern.compile("<[^>]+>");

  private static String removeHtmlTags(String s) {
    return HTML_TAGS.matcher(s).replaceAll("");
  }

  private static final Pattern MATH = Pattern.compile("&lt;math&gt;.*?&lt;/math&gt",
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

  private static String removeMath(String s) {
    return MATH.matcher(s).replaceAll("");
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

  private static final Pattern HEADINGS = Pattern.compile("=+\\s?(.*?)=+");

  private static String removeHeadings(String s) {
    // Make sure there's an extra newline after headings.
    return HEADINGS.matcher(s).replaceAll("$1\n");
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



  private static final Pattern BR = Pattern.compile("&lt;br */&gt;");
  private static final Pattern REF1 = Pattern.compile("&lt;ref[^/]+/&gt;", Pattern.DOTALL);
  private static final Pattern REF2 = Pattern.compile("&lt;ref.*?&lt;/ref&gt;", Pattern.DOTALL);

  public static String removeRefs(String s) {
    s = BR.matcher(s).replaceAll("");     // See test case for why we do this.
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

  protected static final class ImageCaptionsRemover {
    private static final int DEFAULT_NO_BRACKET = 0;
    private static final int STATE_1CLOSE_BRACKET = 1;
    private static final int STATE_1OPEN_BRACKET = 2;

    protected static String remove(String s) {
      String[] labels = { "[[File:", "[[Image:" };
      for (String label : labels) {
        s = removeLabel(s, label);
      }
      return s;
    }

    // This method encodes a finite state machine to handle links in caption, which result in
    // nested [[ ... [[foo]] ... ]] constructs.
    protected static String removeLabel(String s, String label) {
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

  protected static final class DoubleBracesRemover {
    private static final int DEFAULT_NO_BRACE = 0;
    private static final int STATE_1CLOSE_BRACE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    // This method encodes a finite state machine to handle nested double braces (e.g., in infoboxes).
    protected static String remove(String s) {
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

  protected static final class TableRemover {
    private static final int DEFAULT = 0;
    private static final int STATE_PIPE = 1;
    private static final int STATE_1OPEN_BRACE = 2;

    protected static String remove(String s) {
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
}
