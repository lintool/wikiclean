/**
 * WikiClean
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

public class WikiCleanBuilder {
  private boolean withTitle = false;
  private boolean withFooter = false;

  public WikiCleanBuilder() {}

  public WikiCleanBuilder withTitle(boolean flag) {
    this.withTitle = flag;
    return this;
  }

  public WikiCleanBuilder withFooter(boolean flag) {
    this.withFooter = flag;
    return this;
  }

  public WikiClean build() {
    WikiClean clean = new WikiClean();
    clean.setWithTitle(withTitle);
    clean.setWithFooter(withFooter);
    
    return clean;
  }
}
