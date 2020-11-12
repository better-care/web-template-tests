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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class EmptynessTest extends AbstractWebTemplateTest {

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
    public void emptyEvaluation() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_specialist_examination.opt");
        String structuredCompositionString = getFileContent("/res/emptyEvaluation.json");
        JsonNode structuredComposition = objectMapper.readTree(structuredCompositionString);
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("content")).hasSize(1);
        assertThat(rawComposition.get("content").get(0).get("name").get("value").asText()).isEqualTo("Жалобы и анамнез заболевания");

    }

    @Test
    public void emptyComposition() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_allergist_examination_child_lanit.opt");
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(Collections.emptyMap()),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("content")).isEmpty();
    }
}
