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

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.wikiclean.WikiClean.WikiLanguage;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class WikiCleanZhTest {
  @Test
  public void testZhId13() throws IOException {
    String raw = FileUtils.readFileToString(new File("src/test/resources/zhwiki-20150423-id13.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiLanguage.ZH).build();
    String content = cleaner.clean(raw);
    //System.out.println(content);
    //Make sure we've removed category links
    assertFalse(content.contains("Category:数学"));
  }

}
