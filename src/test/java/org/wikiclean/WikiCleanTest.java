package org.wikiclean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WikiCleanTest {

  @Test
  public void testScrewyRefs() {
    String s = "Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism.&lt;ref&gt;Avrich, Paul. ''Anarchist Voices: An Oral History of Anarchism in America'', Princeton University Press 1996 ISBN 0-691-04494-5, p.6&lt;br /&gt;''Blackwell Encyclopaedia of Political Thought'', Blackwell Publishing 1991 ISBN 0-631-17944-5, p. 11.&lt;/ref&gt; Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;&lt;ref&gt;Pierre-Joseph Proudhon. ''What Is Property?'' Princeton, MA: Benjamin R. Tucker, 1876. p. 281.&lt;/ref&gt;";
    assertEquals("Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism. Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;",
        WikiClean.removeRefs(s));
  }

  @Test
  public void testRemoveImageCaption() throws Exception {
    assertEquals("abc", WikiClean.ImageCaptionsRemover.remove("[[File: blah blah]]abc"));
    assertEquals("abc", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah blah]]"));
    assertEquals("", WikiClean.ImageCaptionsRemover.remove("[[File: blah blah]]"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah blah]]def"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: [ ] [ ] [ [ ] ]]def"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah [[nesting]] blah]]def"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah [[nesting [[ ]] ]] blah]]def"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah [[nesting]] [[blah]]]]def"));

    assertEquals("", WikiClean.ImageCaptionsRemover.remove("[[File: blah[[[[]]]] blah]]"));

    // Unbalanced, removes everything until the end.
    assertEquals("abc", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah [[nesting blah]]def"));

    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: here]][[File: blah blah]]def"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: here]]d[[File: blah blah]]ef"));
    assertEquals("", WikiClean.ImageCaptionsRemover.remove("[[File: here]][[File: blah blah]]"));
    assertEquals("abcdef", WikiClean.ImageCaptionsRemover.remove("abc[[File: [[ blah ]] here]][[File: blah blah]]def"));

    // Sprinkle in non-ASCII characters to make sure everything still works.
    assertEquals("abc政府def", WikiClean.ImageCaptionsRemover.remove("abc[[File: 政府 blah [[nesting]] blah政府]]政府def"));
    assertEquals("abc政府def", WikiClean.ImageCaptionsRemover.remove("abc[[File: blah [[nesting [[政府]] [政府[ ]x] ]] blah]]政府def"));
  }

  @Test
  public void testRemoveDoubleBraces() throws Exception {
    assertEquals("abc", WikiClean.DoubleBracesRemover.remove("{{blah blah}}abc"));
    assertEquals("abc", WikiClean.DoubleBracesRemover.remove("abc{{blah blah}}"));
    assertEquals("", WikiClean.DoubleBracesRemover.remove("{{blah blah}}"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{blah blah}}def"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{{ } { } { } }}def"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{blah {{nesting}} blah}}def"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{blah {{nesting {{ }} }} blah}}def"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{blah {{nesting}} {{blah}}}}def"));

    assertEquals("", WikiClean.DoubleBracesRemover.remove("{{blah{{{{}}}} blah}}"));

    // Unbalanced, removes everything until the end.
    assertEquals("abc", WikiClean.DoubleBracesRemover.remove("abc{{blah {{nesting blah}}def"));

    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{here}}{{blah blah}}def"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{here}}d{{blah blah}}ef"));
    assertEquals("", WikiClean.DoubleBracesRemover.remove("{{here}}{{blah blah}}"));
    assertEquals("abcdef", WikiClean.DoubleBracesRemover.remove("abc{{{{ blah }} here}}{{blah blah}}def"));

    // Sprinkle in non-ASCII characters to make sure everything still works.
    assertEquals("abc政府def", WikiClean.DoubleBracesRemover.remove("abc{{政府 blah {{nesting}} blah政府}}政府def"));
    assertEquals("abc政府def", WikiClean.DoubleBracesRemover.remove("abc{{blah {{nesting {{政府}} [政府[ ]x] }} blah}}政府def"));
  }

  @Test
  public void testRemoveTables() throws Exception {
    assertEquals("abc", WikiClean.TableRemover.remove("{|blah blah|}abc"));
    assertEquals("abc", WikiClean.TableRemover.remove("abc{|blah blah|}"));
    assertEquals("", WikiClean.TableRemover.remove("{|blah blah|}"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|blah blah|}def"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|| | | | | | |}def"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|blah {|nesting|} blah|}def"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|blah {|nesting {| | | |} |} blah|}def"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|blah {|nesting|} {|blah|}|}def"));

    assertEquals("", WikiClean.TableRemover.remove("{|blah{|{||}|} blah|}"));

    // Unbalanced, removes everything until the end.
    assertEquals("abc", WikiClean.TableRemover.remove("abc{|blah {|nesting blah|}def"));

    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|here|}{|blah blah|}def"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|here|}d{|blah blah|}ef"));
    assertEquals("", WikiClean.TableRemover.remove("{|here|}{|blah blah|}"));
    assertEquals("abcdef", WikiClean.TableRemover.remove("abc{|{| blah |} here|}{|blah blah|}def"));

    // Sprinkle in non-ASCII characters to make sure everything still works.
    assertEquals("abc政府def", WikiClean.TableRemover.remove("abc{|政府 blah {|nesting|} blah政府|}政府def"));
    assertEquals("abc政府def", WikiClean.TableRemover.remove("abc{|blah {|nesting {|政府|} [政府[ ]x] |} blah|}政府def"));
  }

  @Test
  public void testId12() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id12.xml"));
    WikiClean cleaner = new WikiCleanBuilder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

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
    assertEquals(49652, content.length(), content.length()/100);
  }

  @Test
  public void testId39() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id39.xml"));
    WikiClean cleaner = new WikiCleanBuilder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure that math is removed.
    assertFalse(content.contains("<math>"));

    // Check to see that the parenthetical has been removed.
    assertTrue(content.contains("Albedo, or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface. "));

    // Make sure the extra HTML tags are removed.
    assertFalse(content.contains("<blockquote>"));
    assertFalse(content.contains("</blockquote>"));

    assertEquals(12359, content.length(), content.length()/100);
  }

  @Test
  public void testId290() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id290.xml"));
    WikiClean cleaner = new WikiCleanBuilder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    assertTrue(content.contains("A  (named a, plural aes) is the first letter and a vowel in the basic modern Latin alphabet. "));
    assertEquals(4462, content.length(), content.length()/100);
  }

  @Test
  public void testId303() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id303.xml"));
    WikiClean cleaner = new WikiCleanBuilder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Unit conversion is very bare-bones:
    // Alabama is the thirtieth-largest state in the United States with {{convert|52419|sqmi|km2|abbr=out|sp=us}} of total area:
    // -->
    // Alabama is the thirtieth-largest state in the United States with 52419 sqmi of total area:
    //
    // A {{convert|5|mi|km|0|adj=on}}-wide meteorite impact crater
    // -->
    // A 5 mi-wide meteorite impact crater
    assertTrue(content.contains("Alabama is the thirtieth-largest state in the United States with 52419 sqmi of total area:"));
    assertTrue(content.contains("A 5 mi-wide meteorite impact crater"));
      
    // Make sure heading isn't mangled.
    assertFalse(content.contains("BankingAlabama"));
    assertTrue(content.contains("Ports\n\nAlabama has one seaport"));

    // Make sure tables are removed.
    assertFalse(content.contains("{|"));
    assertFalse(content.contains("|}"));

    assertEquals(50259, content.length(), content.length()/100);
  }

  @Test
  public void testId586() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id586.xml"));
    WikiClean cleaner = new WikiCleanBuilder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // This article has nested tables, make sure they are properly handled.
    assertFalse(content.contains("|}"));
    assertFalse(content.contains("{|"));

    // Make sure headings are properly spaced.
    assertTrue(content.contains("Order\n\nASCII-code order"));
    assertTrue(content.contains("Unicode\n\nUnicode and the ISO/IEC"));

    assertEquals(23356, content.length(), content.length()/100);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanTest.class);
  }
}
