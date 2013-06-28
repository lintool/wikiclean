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
