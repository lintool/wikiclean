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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WikipediaArticlesDumpTest {
  @Test(expected = NoSuchElementException.class)
  public void testReadArticles1() throws IOException {
    WikipediaArticlesDump wikipedia =
        new WikipediaArticlesDump(new File("src/test/resources/article-stubs.xml.bz2"));

    WikiClean cleaner = new WikiClean.Builder().withLanguage(WikiClean.WikiLanguage.EN)
        .withTitle(false).withFooter(false).build();

    String article;
    Iterator<String> iter = wikipedia.iterator();

    // Multiple calls should be okay.
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    article = iter.next();
    assertEquals("Article1", cleaner.getTitle(article));
    assertEquals("1", cleaner.getId(article));

    article = iter.next();
    assertEquals("Article2", cleaner.getTitle(article));
    assertEquals("2", cleaner.getId(article));

    article = iter.next();
    assertEquals("Article3", cleaner.getTitle(article));
    assertEquals("3", cleaner.getId(article));

    // Multiple calls should be okay.
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    article = iter.next();
    assertEquals("Article4", cleaner.getTitle(article));
    assertEquals("4", cleaner.getId(article));

    // Multiple calls should be okay.
    assertTrue(iter.hasNext());
    assertTrue(iter.hasNext());

    article = iter.next();
    assertEquals("Article5", cleaner.getTitle(article));
    assertEquals("5", cleaner.getId(article));

    // We've reached the end.
    assertFalse(iter.hasNext());
    assertFalse(iter.hasNext());
    assertFalse(iter.hasNext());

    // Should get a NoSuchElementException.
    iter.next();
  }
}
