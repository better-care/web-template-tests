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

package care.better.platform.web.template.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author Primoz Delopst
 */

@SuppressWarnings({"InterfaceNeverImplemented", "unused"})
public interface CompositionConverter {

    /**
     * Converts RAW composition json string to the FLAT composition json string.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param rawComposition  RAW composition json string
     * @return FLAT composition json string
     */
    String convertRawToFlat(String template, String defaultLanguage, String rawComposition) throws Exception;

    /**
     * Converts RAW composition json string to the STRUCTURED composition json string.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param rawComposition  RAW composition json string
     * @return STRUCTURED composition json string
     */
    String convertRawToStructured(String template, String defaultLanguage, String rawComposition) throws Exception;

    /**
     * Converts FLAT composition json string to the RAW composition json string.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param flatComposition FLAT composition json string
     * @return RAW composition json string
     */
    String convertFlatToRaw(String template, String defaultLanguage, String flatComposition) throws Exception;

    /**
     * Converts FLAT composition json string to the STRUCTURED composition json string.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param flatComposition FLAT composition json string
     * @return STRUCTURED composition json string
     */
    String convertFlatToStructured(String template, String defaultLanguage, String flatComposition) throws Exception;

    /**
     * Converts STRUCTURED composition json string to the RAW composition json string.
     *
     * @param template              Template xml string
     * @param defaultLanguage       Template default language
     * @param structuredComposition STRUCTURED composition json string
     * @return RAW composition json string
     */
    String convertStructuredToRaw(String template, String defaultLanguage, String structuredComposition) throws Exception;

    /**
     * Converts STRUCTURED composition json string to the FLAT composition json string.
     *
     * @param template              Template xml string
     * @param defaultLanguage       Template default language
     * @param structuredComposition STRUCTURED composition json string
     * @return FLAT composition json string
     */
    String convertStructuredToFlat(String template, String defaultLanguage, String structuredComposition) throws Exception;

    /**
     * Converts RAW composition json string to the FLAT composition.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param rawComposition  RAW composition json string
     * @return FLAT composition
     */
    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    default Map<String, Object> convertRawToFlat(
            String template,
            String defaultLanguage,
            String rawComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertRawToFlat(template, defaultLanguage, rawComposition), new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Converts RAW composition json string to the STRUCTURED composition.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param rawComposition  RAW composition json string
     * @return STRUCTURED composition
     */
    default JsonNode convertRawToStructured(
            String template,
            String defaultLanguage,
            String rawComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertRawToStructured(template, defaultLanguage, rawComposition), JsonNode.class);
    }


    /**
     * Converts FLAT composition json string to the RAW composition.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param flatComposition FLAT composition json string
     * @return RAW composition
     */
    default JsonNode convertFlatToRaw(
            String template,
            String defaultLanguage,
            String flatComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertFlatToRaw(template, defaultLanguage, flatComposition), JsonNode.class);

    }

    /**
     * Converts STRUCTURED composition json string to the FLAT composition.
     *
     * @param template              Template xml string
     * @param defaultLanguage       Template default language
     * @param structuredComposition STRUCTURED composition json string
     * @return FLAT composition
     */
    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    default Map<String, Object> convertStructuredToFlat(
            String template,
            String defaultLanguage,
            String structuredComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertFlatToStructured(template, defaultLanguage, structuredComposition), new TypeReference<Map<String, Object>>() {});
    }

    /**
     * Converts FLAT composition json string to the STRUCTURED composition.
     *
     * @param template        Template xml string
     * @param defaultLanguage Template default language
     * @param flatComposition FLAT composition json string
     * @return STRUCTURED composition
     */
    default JsonNode convertFlatToStructured(
            String template,
            String defaultLanguage,
            String flatComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertStructuredToFlat(template, defaultLanguage, flatComposition), JsonNode.class);
    }

    /**
     * Converts STRUCTURED composition json string to the RAW composition.
     *
     * @param template              Template xml string
     * @param defaultLanguage       Template default language
     * @param structuredComposition STRUCTURED composition json string
     * @return RAW composition
     */
    default JsonNode convertStructuredToRaw(
            String template,
            String defaultLanguage,
            String structuredComposition,
            ObjectMapper objectMapper) throws Exception {
        return objectMapper.readValue(convertStructuredToRaw(template, defaultLanguage, structuredComposition), JsonNode.class);
    }
}
