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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.wikiclean.WikiClean.WikiLanguage;

public class WikiCleanDeTest {

  @Test
  public void testId1() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id1.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure categories are removed properly.
    assertFalse(content.contains("Kategorie:Pseudonym"));

    // Make sure footer is removed properly.
    assertFalse(content.contains("Referenzen"));
    assertFalse(content.contains("Weblinks"));
    assertFalse(content.contains("Literatur"));

    assertEquals(4444, content.length());
  }

  @Test
  public void testId5() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id5.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure footer is removed properly.
    assertFalse(content.contains("Einzelnachweise"));
    assertFalse(content.contains("Siehe auch"));

    // Make sure [[Datei:... is properly handled.
    assertFalse(content.contains("Filmfestspielen von Venedig 2009"));

    assertEquals(9849, content.length());
  }

  @Test
  public void testId81() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id81.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Check handling of IPA and other parentheticals.
    // TODO: Need better handling here.
    assertFalse(content.contains("[]"));
    assertEquals(19563, content.length());
  }

  @Test
  public void testId89() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id89.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    assertFalse(content.contains("Quellen"));
    assertEquals(2417, content.length(), content.length());
  }

  @Test
  public void testId111() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id111.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Check handling of IPA and other parentheticals.
    // TODO: Need better handling here.
    assertFalse(content.contains("[]"));

    // Check proper handling of indentation.
    assertTrue(content.contains("\n1 Ampere = 1 Coulomb pro Sekunde"));

    assertEquals(2560, content.length());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanDeTest.class);
  }
}
