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
  public void testScrewyRefs() {
    String s = "Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism.&lt;ref&gt;Avrich, Paul. ''Anarchist Voices: An Oral History of Anarchism in America'', Princeton University Press 1996 ISBN 0-691-04494-5, p.6&lt;br /&gt;''Blackwell Encyclopaedia of Political Thought'', Blackwell Publishing 1991 ISBN 0-631-17944-5, p. 11.&lt;/ref&gt; Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;&lt;ref&gt;Pierre-Joseph Proudhon. ''What Is Property?'' Princeton, MA: Benjamin R. Tucker, 1876. p. 281.&lt;/ref&gt;";
    assertEquals("Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism. Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;",
        WikiCleaner.removeRefs(s));
  }
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

    //System.out.println(content);

    assertTrue(content.contains("Anarchism is generally defined as the political philosophy which holds the state to be undesirable, unnecessary, and harmful, or alternatively as opposing authority and hierarchical organization in the conduct of human relations. Proponents of anarchism, known as \"anarchists\", advocate stateless societies based on non-hierarchical voluntary associations.\n"));
    assertTrue(content.contains("There are many types and traditions of anarchism, not all of which are mutually exclusive. Anarchist schools of thought can differ fundamentally, supporting anything from extreme individualism to complete collectivism."));
    assertEquals(49652, content.length(), 100);
  }

  @Test
  public void testId39() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id39.xml"));
    String content = WikiCleaner.clean(raw);
    //System.out.println(content);

    // Make sure that math is removed.
    assertFalse(content.contains("<math>"));

    // Check to see that the parenthetical has been removed.
    assertTrue(content.contains("Albedo, or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface. "));

    // Make sure the extra HTML tags are removed.
    assertFalse(content.contains("<blockquote>"));
    assertFalse(content.contains("</blockquote>"));

    assertEquals(12360, content.length(), 100);
  }

  @Test
  public void testId290() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id290.xml"));
    String content = WikiCleaner.clean(raw);
    //System.out.println(content);

    assertTrue(content.contains("A  (named a, plural aes) is the first letter and a vowel in the basic modern Latin alphabet. "));
    assertEquals(4462, content.length(), 100);
  }

  @Test
  public void testId303() throws Exception {
    String raw = FileUtils.readFileToString(
        new File("src/test/java/org/wikiclean/enwiki-20120104-id303.xml"));
    String content = WikiCleaner.clean(raw);
    //System.out.println(content);

    // Known issue: This isn't handled properly:
    // A {{convert|5|mi|km|0|adj=on}}-wide meteorite impact crater

    // Make sure heading isn't mangled.
    assertFalse(content.contains("BankingAlabama"));

    // Make sure tables are removed.
    assertFalse(content.contains("{|"));
    assertFalse(content.contains("|}"));

    assertEquals(50179, content.length(), 100);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanerTest.class);
  }
}
