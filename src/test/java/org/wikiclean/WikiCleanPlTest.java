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
import org.wikiclean.languages.Catalan;
import org.wikiclean.languages.Polish;

import java.io.File;

import static org.junit.Assert.assertFalse;

public class WikiCleanPlTest {

  @Test
  public void testArticle() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/plwiki-test.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(new Polish()).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);

    // Make sure categories are removed properly.
    assertFalse(content.contains("Kategoria:Zmarli w 1887"));

    // Make sure footer is removed properly.
    assertFalse(content.contains("Linki zewnętrzne"));
  }


  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanPlTest.class);
  }
}
