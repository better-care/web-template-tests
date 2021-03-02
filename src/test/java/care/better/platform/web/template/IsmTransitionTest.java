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
public class IsmTransitionTest extends AbstractWebTemplateTest {

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
    public void careflowStepBuilder() throws Exception {
        String template = getFileContent("/res/MED - Medication Order.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.<String, Object>of(
                                "medication_order/medication_detail/medication_action/ism_transition/careflow_step", "at0001",
                                "medication_order/medication_detail/medication_action/medicine", "Aspirin")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);
        
        JsonNode action = rawComposition.get("content").get(0).get("items").get(0);
        JsonNode currentState = action.get("ism_transition").get("current_state");
        assertThat(currentState.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(currentState.get("defining_code").get("code_string").asText()).isEqualTo("524");
        assertThat(currentState.get("value").asText()).isEqualTo("initial");

        JsonNode careflowStep = action.get("ism_transition").get("careflow_step");
        assertThat(careflowStep.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("local");
        assertThat(careflowStep.get("defining_code").get("code_string").asText()).isEqualTo("at0001");
        assertThat(careflowStep.get("value").asText()).isEqualTo("*Plan medication(en)");
    }

    @Test
    public void careflowStepBuilderMultipleCurrentState() throws Exception {
        String template = getFileContent("/res/MED - Medication Order.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.<String, Object>of(
                                "medication_order/medication_detail/medication_action/ism_transition/careflow_step", "at0002",
                                "medication_order/medication_detail/medication_action/medicine", "Aspirin")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);
        
        JsonNode action = rawComposition.get("content").get(0).get("items").get(0);
        JsonNode currentState = action.get("ism_transition").get("current_state");
        assertThat(currentState.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(currentState.get("defining_code").get("code_string").asText()).isEqualTo("245");
        assertThat(currentState.get("value").asText()).isEqualTo("active");

        JsonNode careflowStep = action.get("ism_transition").get("careflow_step");
        assertThat(careflowStep.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("local");
        assertThat(careflowStep.get("defining_code").get("code_string").asText()).isEqualTo("at0002");
        assertThat(careflowStep.get("value").asText()).isEqualTo("*Issue prescription for medication(en)");
    }

    @Test
    public void careflowStepBuilderMultipleCurrentStateOverride() throws Exception {
        String template = getFileContent("/res/MED - Medication Order.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.<String, Object>of(
                                "medication_order/medication_detail/medication_action/ism_transition/careflow_step", "at0002",
                                "medication_order/medication_detail/medication_action/ism_transition/current_state|value", "initial",
                                "medication_order/medication_detail/medication_action/medicine", "Aspirin")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);
       
        JsonNode action1 = rawComposition.get("content").get(0).get("items").get(0);
        JsonNode currentState1 = action1.get("ism_transition").get("current_state");
        assertThat(currentState1.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(currentState1.get("defining_code").get("code_string").asText()).isEqualTo("524");
        assertThat(currentState1.get("value").asText()).isEqualTo("initial");

        JsonNode careflowStep1 = action1.get("ism_transition").get("careflow_step");
        assertThat(careflowStep1.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("local");
        assertThat(careflowStep1.get("defining_code").get("code_string").asText()).isEqualTo("at0002");
        assertThat(careflowStep1.get("value").asText()).isEqualTo("*Issue prescription for medication(en)");

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.<String, Object>of(
                                "medication_order/medication_detail/medication_action/ism_transition/careflow_step", "at0002",
                                "medication_order/medication_detail/medication_action/ism_transition/current_state", "524",
                                "medication_order/medication_detail/medication_action/medicine", "Aspirin")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        JsonNode action2 = rawComposition.get("content").get(0).get("items").get(0);
        JsonNode currentState2 = action2.get("ism_transition").get("current_state");
        assertThat(currentState2.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(currentState2.get("defining_code").get("code_string").asText()).isEqualTo("524");
        assertThat(currentState2.get("value").asText()).isEqualTo("initial");

        JsonNode careflowStep2 = action2.get("ism_transition").get("careflow_step");
        assertThat(careflowStep2.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("local");
        assertThat(careflowStep2.get("defining_code").get("code_string").asText()).isEqualTo("at0002");
        assertThat(careflowStep2.get("value").asText()).isEqualTo("*Issue prescription for medication(en)");
    }
}
