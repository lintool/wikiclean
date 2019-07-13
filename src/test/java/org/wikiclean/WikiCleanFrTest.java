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
import org.wikiclean.WikiClean.WikiLanguage;
import org.wikiclean.languages.French;

import java.io.File;

import static org.junit.Assert.*;

public class WikiCleanFrTest {

  @Test
  public void testId3() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/frwiki-20121230-id3.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(new French()).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure categories are removed properly.
    assertFalse(content.contains("Catégorie:Linguiste français"));

    // Make sure footer is removed properly.
    assertFalse(content.contains("Voir aussi"));
  }


  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanFrTest.class);
  }
}
