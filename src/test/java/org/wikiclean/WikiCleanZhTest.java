package org.wikiclean;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.wikiclean.WikiClean.WikiLanguage;

public class WikiCleanZhTest {
  @Test
  public void testZhId13() throws IOException {
    String raw = FileUtils.readFileToString(new File("src/test/resources/zhwiki-20150423-id13.xml"), "UTF-8");
    WikiClean cleaner = new WikiCleanBuilder().withLanguage(WikiLanguage.ZH).build();
    String content = cleaner.clean(raw);
    System.out.println(content);
    //Make sure we've removed category links
    assertFalse(content.contains("Category:数学"));
  }

}
