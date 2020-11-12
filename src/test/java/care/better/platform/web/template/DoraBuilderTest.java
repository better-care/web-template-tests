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
import com.fasterxml.jackson.core.type.TypeReference;
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
public class DoraBuilderTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    @Test
    public void occurencesBug() throws Exception {
        String template = getFileContent("/res/TMDS Breast - Radiographer Mammography Report.xml");
        String flatCompositionString = getFileContent("/res/Radiographer_Mammography.json");

        Map<String, Object> flatComposition = objectMapper.readValue(flatCompositionString, new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "en", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition.path("radiographer_mammography_report")
                           .path("past_therapies").path(0)
                           .path("breast-related_interventions_tmds").path(0)
                           .path("ism_transition").path(0)
                           .path("current_state").path(0).path("|code")
                           .asText()).isEqualTo("524");
        assertThat(structuredComposition.path("radiographer_mammography_report")
                           .path("past_procedures").path(0)
                           .path("breast-related_interventions_tmds").path(1)
                           .path("ism_transition").path(0)
                           .path("current_state").path(0).path("|code")
                           .asText()).isEqualTo("524");
        assertThat(structuredComposition.path("radiographer_mammography_report")
                           .path("procedure_details").path(0)
                           .path("procedure_details").path(0)
                           .path("ism_transition").path(0)
                           .path("current_state").path(0).path("|code")
                           .asText()).isEqualTo("526");
    }
}
