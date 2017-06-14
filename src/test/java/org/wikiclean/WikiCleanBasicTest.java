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
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WikiCleanBasicTest {

  @Test
  public void testScrewyRefs() {
    // Using reflection to test private methods, usually bad practice...
    // See: https://stackoverflow.com/questions/34571/how-do-i-test-a-class-that-has-private-methods-fields-or-inner-classes
    try {
      String s = "Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism.&lt;ref&gt;Avrich, Paul. ''Anarchist Voices: An Oral History of Anarchism in America'', Princeton University Press 1996 ISBN 0-691-04494-5, p.6&lt;br /&gt;''Blackwell Encyclopaedia of Political Thought'', Blackwell Publishing 1991 ISBN 0-631-17944-5, p. 11.&lt;/ref&gt; Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;&lt;ref&gt;Pierre-Joseph Proudhon. ''What Is Property?'' Princeton, MA: Benjamin R. Tucker, 1876. p. 281.&lt;/ref&gt;";

      Class[] classArgs = new Class[1];
      classArgs[0] = String.class;
      Method method = WikiClean.class.getDeclaredMethod("removeRefs", classArgs);
      method.setAccessible(true);

      assertEquals("Mutualism has been retrospectively characterised as ideologically situated between individualist and collectivist forms of anarchism. Proudhon first characterised his goal as a &quot;third form of society, the synthesis of communism and property.&quot;",
          method.invoke(s));
    } catch (Exception e) {}
  }

  @Test
  public void testRemoveImageCaption() throws Exception {
    try {
      // Accessing non-visible classes with reflection
      // https://stackoverflow.com/questions/15015675/accessing-non-visible-classes-with-reflection
      Class<?> innerClazz = Class.forName("org.wikiclean.WikiClean$ImageCaptionsRemover");

      Class[] classArgs = new Class[1];
      classArgs[0] = String.class;
      Method method = innerClazz.getDeclaredMethod("remove", classArgs);
      method.setAccessible(true);

      assertEquals("abc", method.invoke("[[File: blah blah]]abc"));
      assertEquals("abc", method.invoke("abc[[File: blah blah]]"));
      assertEquals("", method.invoke("[[File: blah blah]]"));
      assertEquals("abcdef", method.invoke("abc[[File: blah blah]]def"));
      assertEquals("abcdef", method.invoke("abc[[File: [ ] [ ] [ [ ] ]]def"));
      assertEquals("abcdef", method.invoke("abc[[File: blah [[nesting]] blah]]def"));
      assertEquals("abcdef", method.invoke("abc[[File: blah [[nesting [[ ]] ]] blah]]def"));
      assertEquals("abcdef", method.invoke("abc[[File: blah [[nesting]] [[blah]]]]def"));

      assertEquals("", method.invoke("[[File: blah[[[[]]]] blah]]"));

      // Unbalanced, removes everything until the end.
      assertEquals("abc", method.invoke("abc[[File: blah [[nesting blah]]def"));

      assertEquals("abcdef", method.invoke("abc[[File: here]][[File: blah blah]]def"));
      assertEquals("abcdef", method.invoke("abc[[File: here]]d[[File: blah blah]]ef"));
      assertEquals("", method.invoke("[[File: here]][[File: blah blah]]"));
      assertEquals("abcdef", method.invoke("abc[[File: [[ blah ]] here]][[File: blah blah]]def"));

      // Sprinkle in non-ASCII characters to make sure everything still works.
      assertEquals("abc政府def", method.invoke("abc[[File: 政府 blah [[nesting]] blah政府]]政府def"));
      assertEquals("abc政府def", method.invoke("abc[[File: blah [[nesting [[政府]] [政府[ ]x] ]] blah]]政府def"));
    } catch (Exception e) {}
  }

  @Test
  public void testRemoveDoubleBraces() throws Exception {
    try {
      // Accessing non-visible classes with reflection
      // https://stackoverflow.com/questions/15015675/accessing-non-visible-classes-with-reflection
      Class<?> innerClazz = Class.forName("org.wikiclean.WikiClean$DoubleBracesRemover");

      Class[] classArgs = new Class[1];
      classArgs[0] = String.class;
      Method method = innerClazz.getDeclaredMethod("remove", classArgs);
      method.setAccessible(true);

      assertEquals("abc", method.invoke("{{blah blah}}abc"));
      assertEquals("abc", method.invoke("abc{{blah blah}}"));
      assertEquals("", method.invoke("{{blah blah}}"));
      assertEquals("abcdef", method.invoke("abc{{blah blah}}def"));
      assertEquals("abcdef", method.invoke("abc{{{ } { } { } }}def"));
      assertEquals("abcdef", method.invoke("abc{{blah {{nesting}} blah}}def"));
      assertEquals("abcdef", method.invoke("abc{{blah {{nesting {{ }} }} blah}}def"));
      assertEquals("abcdef", method.invoke("abc{{blah {{nesting}} {{blah}}}}def"));

      assertEquals("", method.invoke("{{blah{{{{}}}} blah}}"));

      // Unbalanced, removes everything until the end.
      assertEquals("abc", method.invoke("abc{{blah {{nesting blah}}def"));

      assertEquals("abcdef", method.invoke("abc{{here}}{{blah blah}}def"));
      assertEquals("abcdef", method.invoke("abc{{here}}d{{blah blah}}ef"));
      assertEquals("", method.invoke("{{here}}{{blah blah}}"));
      assertEquals("abcdef", method.invoke("abc{{{{ blah }} here}}{{blah blah}}def"));

      // Sprinkle in non-ASCII characters to make sure everything still works.
      assertEquals("abc政府def", method.invoke("abc{{政府 blah {{nesting}} blah政府}}政府def"));
      assertEquals("abc政府def", method.invoke("abc{{blah {{nesting {{政府}} [政府[ ]x] }} blah}}政府def"));
    } catch (Exception e) {}
  }

  @Test
  public void testRemoveTables() throws Exception {
    try {
      // Accessing non-visible classes with reflection
      // https://stackoverflow.com/questions/15015675/accessing-non-visible-classes-with-reflection
      Class<?> innerClazz = Class.forName("org.wikiclean.WikiClean$TableRemover");

      Class[] classArgs = new Class[1];
      classArgs[0] = String.class;
      Method method = innerClazz.getDeclaredMethod("remove", classArgs);
      method.setAccessible(true);

      assertEquals("abc", method.invoke("{|blah blah|}abc"));
      assertEquals("abc", method.invoke("abc{|blah blah|}"));
      assertEquals("", method.invoke("{|blah blah|}"));
      assertEquals("abcdef", method.invoke("abc{|blah blah|}def"));
      assertEquals("abcdef", method.invoke("abc{|| | | | | | |}def"));
      assertEquals("abcdef", method.invoke("abc{|blah {|nesting|} blah|}def"));
      assertEquals("abcdef", method.invoke("abc{|blah {|nesting {| | | |} |} blah|}def"));
      assertEquals("abcdef", method.invoke("abc{|blah {|nesting|} {|blah|}|}def"));

      assertEquals("", method.invoke("{|blah{|{||}|} blah|}"));

      // Unbalanced, removes everything until the end.
      assertEquals("abc", method.invoke("abc{|blah {|nesting blah|}def"));

      assertEquals("abcdef", method.invoke("abc{|here|}{|blah blah|}def"));
      assertEquals("abcdef", method.invoke("abc{|here|}d{|blah blah|}ef"));
      assertEquals("", method.invoke("{|here|}{|blah blah|}"));
      assertEquals("abcdef", method.invoke("abc{|{| blah |} here|}{|blah blah|}def"));

      // Sprinkle in non-ASCII characters to make sure everything still works.
      assertEquals("abc政府def", method.invoke("abc{|政府 blah {|nesting|} blah政府|}政府def"));
      assertEquals("abc政府def", method.invoke("abc{|blah {|nesting {|政府|} [政府[ ]x] |} blah|}政府def"));
    } catch (Exception e) {}
  }

  @Test
  public void testBuilderOptions() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/enwiki-20120104-id12.xml"), "UTF-8");
    WikiClean cleaner;
    String content;

    // Keep the footer.
    cleaner = new WikiClean.Builder().withFooter(true).build();
    content = cleaner.clean(raw);

    assertTrue(content.contains("See also"));
    assertTrue(content.contains("Reference"));
    assertTrue(content.contains("Further reading"));
    assertTrue(content.contains("External links"));

    assertEquals(true, cleaner.withFooter());
    assertEquals(false, cleaner.withTitle());

    // Explicitly not keep the footer.
    cleaner = new WikiClean.Builder().withFooter(false).build();
    content = cleaner.clean(raw);

    assertFalse(content.contains("See also"));
    assertFalse(content.contains("Reference"));
    assertFalse(content.contains("Further reading"));
    assertFalse(content.contains("External links"));

    assertEquals(false, cleaner.withFooter());
    assertEquals(false, cleaner.withTitle());

    // Print the title.
    cleaner = new WikiClean.Builder().withTitle(true).build();
    content = cleaner.clean(raw);

    assertTrue(content.contains("Anarchism\n\nAnarchism is generally"));

    assertEquals(false, cleaner.withFooter());
    assertEquals(true, cleaner.withTitle());

    // Explicitly not print the title.
    cleaner = new WikiClean.Builder().withTitle(false).build();
    content = cleaner.clean(raw);

    assertFalse(content.contains("Anarchism\n\nAnarchism is generally"));

    assertEquals(false, cleaner.withFooter());
    assertEquals(false, cleaner.withTitle());

    // Keep the footer and title.
    cleaner = new WikiClean.Builder().withTitle(true).withFooter(true).build();
    content = cleaner.clean(raw);

    assertTrue(content.contains("See also"));
    assertTrue(content.contains("Reference"));
    assertTrue(content.contains("Further reading"));
    assertTrue(content.contains("External links"));
    assertTrue(content.contains("Anarchism\n\nAnarchism is generally"));

    assertEquals(true, cleaner.withFooter());
    assertEquals(true, cleaner.withTitle());

    // Should be same as the default.
    cleaner = new WikiClean.Builder().withTitle(false).withFooter(false).build();
    content = cleaner.clean(raw);

    assertFalse(content.contains("See also"));
    assertFalse(content.contains("Reference"));
    assertFalse(content.contains("Further reading"));
    assertFalse(content.contains("External links"));
    assertFalse(content.contains("Anarchism\n\nAnarchism is generally"));

    assertEquals(false, cleaner.withFooter());
    assertEquals(false, cleaner.withTitle());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanBasicTest.class);
  }
}
