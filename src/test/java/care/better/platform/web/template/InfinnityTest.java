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
import care.better.platform.web.template.validator.ValidationErrorDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class InfinnityTest extends AbstractWebTemplateTest {

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
    public void validationErrorInfinnityTest() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_neurologist_examination(1-17)_lanit.v1-new.opt");
        String flatCompositionString = getFileContent("/res/openEHR-EHR-COMPOSITION.t_neurologist_examination(1-17)_lanit.v1.json");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                flatCompositionString,
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "INFINNITYSAMPLES",
                        CompositionBuilderContextKey.ENCODING.getKey(), "UTF-8",
                        CompositionBuilderContextKey.ISM_TRANSITION.getKey(), "openehr::524::initial"),
                objectMapper)).isInstanceOf(Exception.class);
    }

    @Test
    public void infinnityTemplateMismatch0_12() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_endocrinologist_examination (0-12).opt");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/0-12.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "INFINNITYSAMPLES"),
                objectMapper);

        List<ValidationErrorDto> validationErrors = getCompositionValidator().validate(template, rawComposition.toString());
        assertThat(validationErrors).hasSize(1);
    }

    @Test
    public void infinnityTemplateMismatch1_17() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_endocrinologist_examination (1-17).opt");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/1-17.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "INFINNITYSAMPLES"),
                objectMapper);

        List<ValidationErrorDto> validationErrors = getCompositionValidator().validate(template, rawComposition.toString());
        assertThat(validationErrors).hasSize(3);
    }

    @Test
    public void ru354() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.prevaccinal_examination.v1.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/composition_with_activity.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "INFINNITYSAMPLES"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
    }
}
