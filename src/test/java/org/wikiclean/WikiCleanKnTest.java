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
import org.wikiclean.languages.Kannada;

import java.io.File;

import static org.junit.Assert.assertFalse;

public class WikiCleanKnTest {
  @Test
  public void testId1() throws Exception {
    String raw = FileUtils.readFileToString(new File("src/test/resources/knwiki-20180614-id1.xml"), "UTF-8");
    WikiClean cleaner = new WikiClean.Builder().withLanguage(new Kannada()).build();
    String content = cleaner.clean(raw);
    // System.out.println(content);

    assertFalse(content.contains("ಉಲ್ಲೇಖ"));
    assertFalse(content.contains("ಕೆಳಗಿನ ಲೇಖನಗಳನ್ನೂ ನೋಡಿ"));
    assertFalse(content.contains("ಇದನ್ನೂ ನೋಡಿ"));
    assertFalse(content.contains("ಹೊರಗಿನ ಸಂಪರ್ಕಗಳು"));

    // Make sure we've removed the inter-wiki links.
    assertFalse(content.contains("[[ಕುವೆಂಪು|ರಾಷ್ಟ್ರಕವಿ ಕುವೆಂಪು]]"));
    assertFalse(content.contains("[[ಡಾ.ಎಸ್.ಆರ್. ರಾವ್]]"));
    assertFalse(content.contains("[[ಯು ಆರ್ ಅನಂತಮೂರ್ತಿ]]"));

    // Make sure we've removed refs.
    assertFalse(content.contains("&lt;ref&gt;[http://www.thehindu.com/news/cities/bangalore/work-on-shimoga-airport-project-yet-to-commence/article6473487.ece ಹಿಂದು ಪತ್ರಿಕೆ ವರದಿ]&lt;/ref&gt;"));

    // Make sure we've removed captions.
    assertFalse(content.contains("Image:Mahishamardhini.jpg"));
    assertFalse(content.contains("File:Jog-falls.jpg"));

    // Make sure we've removed refs.
    assertFalse(content.contains("ಹಿಂದು ಪತ್ರಿಕೆ ವರದಿ"));

    // Make sure we've removed emphasis.
    assertFalse(content.contains("'''ಶಿವಮೊಗ್ಗ'''"));

    // Make sure we've removed headings.
    assertFalse(content.contains("== ಪ್ರವಾಸೀ ತಾಣಗಳು =="));
    assertFalse(content.contains("== ನದಿಗಳು=="));

    // Make sure we've removed category links.
    assertFalse(content.contains("ವರ್ಗ:ಕರ್ನಾಟಕದ"));
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(WikiCleanKnTest.class);
  }
}
