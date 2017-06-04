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

import junit.framework.JUnit4TestAdapter;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WikiCleanEnTest {
  @Test
  public void testId6() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/simplewiki-20161220-id6.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);

    assertFalse(content.contains("Related pages"));
  }

  @Test
  public void testId12() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id12.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
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
    assertEquals(49655, content.length());
  }

  @Test
  public void testId39() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id39.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure that math is removed.
    assertFalse(content.contains("<math>"));

    // Make sure indentation in handled.
    assertFalse(content.contains("\n:"));

    // Check to see that the parenthetical has been removed.
    assertTrue(content.contains("Albedo, or reflection coefficient, is the diffuse reflectivity or reflecting power of a surface. "));

    // Make sure the extra HTML tags are removed.
    assertFalse(content.contains("<blockquote>"));
    assertFalse(content.contains("</blockquote>"));

    assertEquals(12347, content.length());
  }

  @Test
  public void testId290() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id290.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure the IPA is properly cleaned.
    assertTrue(content.contains("A (named a, plural aes) is the first letter and a vowel in the basic modern Latin alphabet. "));

    assertEquals(4465, content.length());
  }

  @Test
  public void testId303() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id303.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
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

    assertEquals(50259, content.length());
  }

  @Test
  public void testId586() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id586.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // This article has nested tables, make sure they are properly handled.
    assertFalse(content.contains("|}"));
    assertFalse(content.contains("{|"));

    // Make sure headings are properly spaced.
    assertTrue(content.contains("Order\n\nASCII-code order"));
    assertTrue(content.contains("Unicode\n\nUnicode and the ISO/IEC"));

    assertEquals(23353, content.length());
  }

  @Test
  public void testId655() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id655.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // This article has a <gallery>, make sure it is properly handled.
    assertFalse(content.contains("File:Gregor Reisch, Margarita Philosophica"));
    assertFalse(content.contains("File:Rekenaar 1553.jpg"));

    assertEquals(17109, content.length());
  }

  @Test
  public void testId718() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id1718.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure NOTOC is properly removed.
    assertFalse(content.contains("__NOTOC__"));

    assertEquals(1851, content.length());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanEnTest.class);
  }
}
