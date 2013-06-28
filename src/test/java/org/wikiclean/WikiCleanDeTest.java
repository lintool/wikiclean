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

import java.io.File;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.wikiclean.WikiClean.WikiLanguage;

public class WikiCleanDeTest {

  @Test
  public void testId1() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id1.xml"));
    WikiClean cleaner = new WikiCleanBuilder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure categories are removed properly.
    assertFalse(content.contains("Kategorie:Pseudonym"));

    // Make sure footer is removed properly.
    assertFalse(content.contains("Referenzen"));
    assertFalse(content.contains("Weblinks"));
    assertFalse(content.contains("Literatur"));

    assertEquals(4444, content.length(), content.length()/50);
  }

  @Test
  public void testId5() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id5.xml"));
    WikiClean cleaner = new WikiCleanBuilder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure footer is removed properly.
    assertFalse(content.contains("Einzelnachweise"));
    assertFalse(content.contains("Siehe auch"));

    // Make sure [[Datei:... is properly handled.
    assertFalse(content.contains("Filmfestspielen von Venedig 2009"));

    assertEquals(9849, content.length(), content.length()/50);
  }

  @Test
  public void testId89() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/dewiki-20130602-id89.xml"));
    WikiClean cleaner = new WikiCleanBuilder().withLanguage(WikiLanguage.DE).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    assertFalse(content.contains("Quellen"));
    assertEquals(2417, content.length(), content.length()/50);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanDeTest.class);
  }
}
