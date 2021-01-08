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

package care.better.platform.web.template.validator;

import java.util.List;

/**
 * @author Primoz Delopst
 */
public interface CompositionValidator {

    /**
     * Validates RAW composition
     *
     * @param template       Template xml string
     * @param rawComposition RAW composition json string
     * @return {@code List} of {@code ValidationErrorDto}
     */
    List<ValidationErrorDto> validate(String template, String rawComposition) throws Exception;

    /**
     * Validates RAW composition with added parameters
     *
     * @param template       Template xml string
     * @param rawComposition RAW composition json string
     * @param strictTextValidation text matching setting
     * @param relaxedNameMatching property name matching setting
     * @return {@code List} of {@code ValidationErrorDto}
     */
    List<ValidationErrorDto> validateWithParams(String template, String rawComposition, boolean strictTextValidation, boolean relaxedNameMatching) throws Exception;
}
