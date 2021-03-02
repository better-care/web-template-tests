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
public class FixedValueTest extends AbstractWebTemplateTest {

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
    public void fixed() throws Exception {
        String template = getFileContent("/res/ZN - Assessment Scales Encounter2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("assessment_scales/pain_assessment/story/pain/exascerbating_factor/factor", "test")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrieve).contains(entry("assessment_scales/pain_assessment/story/pain:0/exascerbating_factor/factor", "test"),
                                      entry("assessment_scales/pain_assessment/story/pain:0/exascerbating_factor/change|ordinal", 2));
    }

    @Test
    public void fixedCodedInDvText() throws Exception {
        String template = getFileContent("/res/Testing Template N1.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("test_encounter/testing/testing/name_1", "hello world!")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrieve).contains(entry("test_encounter/testing:0/testing:0/testing_dv_text|value", "Hello world"),
                                      entry("test_encounter/testing:0/testing:0/testing_dv_text|terminology", "LOINC"),
                                      entry("test_encounter/testing:0/testing:0/testing_dv_text|code", "1234"));
    }
}
