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

package care.better.platform.web.template;

import care.better.platform.web.template.context.CompositionBuilderContextKey;
import care.better.platform.web.template.extension.WebTemplateTestExtension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class AnyTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void anyElementQuantity() throws Exception {
        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "en")
                .put("ctx/territory", "IE")
                .put("ctx/composer_name", "John")
                .put("encounter/test_any/any_element/quantity_value|magnitude", "300")
                .put("encounter/test_any/any_element/quantity_value|unit", "mm")
                .build();

        String template = getFileContent("/res/any_element.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatCompositionMap),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
        assertThat(getValueNode(rawComposition).get("magnitude").asDouble()).isEqualTo(300.0);
        assertThat(getValueNode(rawComposition).get("units").asText()).isEqualTo("mm");

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/quantity_value|magnitude", 300.0));
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/quantity_value|unit", "mm"));
    }

    @Test
    public void anyElementText() throws Exception {
        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "en")
                .put("ctx/territory", "IE")
                .put("ctx/composer_name", "John")
                .put("encounter/test_any/any_element/text_value", "Hello world!")
                .build();

        String template = getFileContent("/res/any_element.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatCompositionMap),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
        assertThat(getValueNode(rawComposition).get("value").asText()).isEqualTo("Hello world!");

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/text_value", "Hello world!"));
    }

    @Test
    public void anyElementCodedText() throws Exception {
        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "en")
                .put("ctx/territory", "IE")
                .put("ctx/composer_name", "John")
                .put("encounter/test_any/any_element/coded_text_value|value", "Hello world!")
                .put("encounter/test_any/any_element/coded_text_value|code", "HW")
                .put("encounter/test_any/any_element/coded_text_value|terminology", "mine")
                .build();

        String template = getFileContent("/res/any_element.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatCompositionMap),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
        assertThat(getValueNode(rawComposition).get("value").asText()).isEqualTo("Hello world!");
        assertThat(getValueNode(rawComposition).get("defining_code").get("code_string").asText()).isEqualTo("HW");
        assertThat(getValueNode(rawComposition).get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("mine");

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/coded_text_value|value", "Hello world!"));
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/coded_text_value|code", "HW"));
        assertThat(flatComposition).contains(entry("encounter/test_any:0/any_element/coded_text_value|terminology", "mine"));
    }

    private JsonNode getValueNode(JsonNode rawComposition) {
        return rawComposition.get("content").get(0).get("data").get("events").get(0).get("data").get("items").get(0).get("value");
    }
}
