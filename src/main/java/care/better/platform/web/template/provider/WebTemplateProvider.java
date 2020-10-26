/* Copyright 2020-2021 Better Ltd (www.better.care)
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

package care.better.platform.web.template.provider;

/**
 * @author Primoz Delopst
 */

@FunctionalInterface
@SuppressWarnings({"InterfaceNeverImplemented", "unused"})
public interface WebTemplateProvider {

    /**
     * Provide web template json string.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @return Template xml string
     */
    String provide(String template, String defaultLanguage) throws Exception;
}
