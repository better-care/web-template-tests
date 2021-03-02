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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class JsonBuilderTest extends AbstractWebTemplateTest {

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
    public void json() throws Exception {
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/JsonBuilder.json"));
        String template = getFileContent("/res/ZN - Vital Functions Encounter.xml");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void rawJson() throws Exception {
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/JsonBuilder2.json"));
        String template = getFileContent("/res/ZN - Vital Functions Encounter.xml");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode retrivedStructuredComposition = getCompositionConverter().convertRawToStructured(template, "en", rawComposition.toString(), objectMapper);
        JsonNode path = retrivedStructuredComposition.path("vital_functions").path("vital_signs").path(0).path("pulse").path(0)
                .path("any_event").path(0).path("heart_rate").path(0);
        assertThat(path.isMissingNode()).isFalse();
        assertThat(path.path("|magnitude").doubleValue()).isEqualTo(90.0);
        assertThat(path.path("|unit").textValue()).isEqualTo("/min");
    }

}
