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

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class LongValueJsonTest extends AbstractWebTemplateTest {

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
    public void longValueJson() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Assessment Scales Encounter2.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("assessment_scales/pain_assessment/story/pain/patient_described_current_intensity/degree_level", 5000000000000000000L)
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
        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "en", rawComposition.toString(), objectMapper);

        assertThat(structuredComposition.get("assessment_scales").isObject()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").isArray()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").isArray()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").get(0).get("pain").isArray()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").get(0)
                           .get("pain").get(0).get("patient_described_current_intensity").isArray()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").get(0)
                           .get("pain").get(0).get("patient_described_current_intensity").get(0).get("degree_level").isArray()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").get(0)
                           .get("pain").get(0).get("patient_described_current_intensity").get(0).get("degree_level").get(0).isLong()).isTrue();
        assertThat(structuredComposition.get("assessment_scales").get("pain_assessment").get(0).get("story").get(0)
                           .get("pain").get(0).get("patient_described_current_intensity").get(0).get("degree_level").get(0).asLong()).isEqualTo(5000000000000000000L);
    }

}
