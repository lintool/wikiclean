package org.wikiclean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WikiCleanerTest {

  @Test
  public void testRemoveImageCaption() throws Exception {
    assertEquals("abc", WikiCleaner.removeImageCaptions("[[File: blah blah]]abc"));
    assertEquals("abc", WikiCleaner.removeImageCaptions("abc[[File: blah blah]]"));
    assertEquals("", WikiCleaner.removeImageCaptions("[[File: blah blah]]"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: blah blah]]def"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: [ ] [ ] [ [ ] ]]def"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: blah [[nesting]] blah]]def"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: blah [[nesting [[ ]] ]] blah]]def"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: blah [[nesting]] [[blah]]]]def"));

    assertEquals("", WikiCleaner.removeImageCaptions("[[File: blah[[[[]]]] blah]]"));

    // Unbalanced, removes everything until the end.
    assertEquals("abc", WikiCleaner.removeImageCaptions("abc[[File: blah [[nesting blah]]def"));

    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: here]][[File: blah blah]]def"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: here]]d[[File: blah blah]]ef"));
    assertEquals("", WikiCleaner.removeImageCaptions("[[File: here]][[File: blah blah]]"));
    assertEquals("abcdef", WikiCleaner.removeImageCaptions("abc[[File: [[ blah ]] here]][[File: blah blah]]def"));

    // Sprinkle in non-ASCII characters to make sure everything still works.
    assertEquals("abc政府def", WikiCleaner.removeImageCaptions("abc[[File: 政府 blah [[nesting]] blah政府]]政府def"));
    assertEquals("abc政府def", WikiCleaner.removeImageCaptions("abc[[File: blah [[nesting [[政府]] [政府[ ]x] ]] blah]]政府def"));
  }

  @Test
  public void testDoubleBraces() throws Exception {
    assertEquals("abc", WikiCleaner.removeDoubleBraces("{{blah blah}}abc"));
    assertEquals("abc", WikiCleaner.removeDoubleBraces("abc{{blah blah}}"));
    assertEquals("", WikiCleaner.removeDoubleBraces("{{blah blah}}"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{blah blah}}def"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{{ } { } { } }}def"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{blah {{nesting}} blah}}def"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{blah {{nesting {{ }} }} blah}}def"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{blah {{nesting}} {{blah}}}}def"));

    assertEquals("", WikiCleaner.removeDoubleBraces("{{blah{{{{}}}} blah}}"));

    // Unbalanced, removes everything until the end.
    assertEquals("abc", WikiCleaner.removeDoubleBraces("abc{{blah {{nesting blah}}def"));

    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{here}}{{blah blah}}def"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{here}}d{{blah blah}}ef"));
    assertEquals("", WikiCleaner.removeDoubleBraces("{{here}}{{blah blah}}"));
    assertEquals("abcdef", WikiCleaner.removeDoubleBraces("abc{{{{ blah }} here}}{{blah blah}}def"));

    // Sprinkle in non-ASCII characters to make sure everything still works.
    assertEquals("abc政府def", WikiCleaner.removeDoubleBraces("abc{{政府 blah {{nesting}} blah政府}}政府def"));
    assertEquals("abc政府def", WikiCleaner.removeDoubleBraces("abc{{blah {{nesting {{政府}} [政府[ ]x] }} blah}}政府def"));
  }

  @Test
  public void testId12() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id12.xml"));
    String content = WikiCleaner.clean(raw);

    // Make sure we've removed the inter-wiki links.
    assertFalse(content.contains("[[af:Anargisme]]"));
    assertFalse(content.contains("[[zh:无政府主义]]"));

    // Make sure we've removed refs.
    assertFalse(content.contains("lt;ref name=&quot;definition&quot;&gt;"));

    // Make sure we've removed captions.
    assertFalse(content.contains("WilliamGodwin.jpg"));

    // Make sure we've removed refs.
    assertFalse(content.contains("Anarcho-communist Joseph Déjacque, the first person to use the term"));

    // Make sure we've removed emphasis.
    assertFalse(content.contains("'''Anarchism''' is generally defined"));

    // Make sure we've removed headings.
    assertFalse(content.contains("==Etymology and terminology=="));
    assertFalse(content.contains("===Origins==="));

    // Make sure we've removed category links.
    assertFalse(content.contains("Category:Political culture"));

    assertTrue(content.contains("Anarchism is generally defined as the political philosophy which holds the state to be undesirable, unnecessary, and harmful, or alternatively as opposing authority and hierarchical organization in the conduct of human relations. Proponents of anarchism, known as \"anarchists\", advocate stateless societies based on non-hierarchical voluntary associations.\n"));
    assertTrue(content.contains("There are many types and traditions of anarchism, not all of which are mutually exclusive. Anarchist schools of thought can differ fundamentally, supporting anything from extreme individualism to complete collectivism."));
  }

  @Test
  public void testId39() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id39.xml"));
    String content = WikiCleaner.clean(raw);

    // Check to see that the parenthetical has been removed.
    assertTrue(content.contains("Albedo, or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface. "));
  }

  @Test
  public void testId290() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id290.xml"));
    String content = WikiCleaner.clean(raw);

    assertTrue(content.contains("A  (named a, plural aes) is the first letter and a vowel in the basic modern Latin alphabet. "));
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanerTest.class);
  }
}
