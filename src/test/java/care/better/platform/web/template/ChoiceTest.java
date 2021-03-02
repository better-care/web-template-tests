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
public class ChoiceTest extends AbstractWebTemplateTest {

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
    public void choiceDvQuantityAndDvInterval() throws Exception {
        String template = getFileContent("/local/MED - Medication Order-choice.opt");

        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "en")
                .put("ctx/territory", "IE")
                .put("ctx/composer_name", "John")
                .put("medication_order/medication_detail/medication_instruction:0/narrative", "Take as prescribed!")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R0")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/structured_dose/quantity/value2/lower|magnitude", "72.36")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/structured_dose/quantity/value2/lower|unit", "1")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/structured_dose/quantity/value2/upper|magnitude", "84.34")
                .put("medication_order/medication_detail/medication_instruction:0/order:0/structured_dose/quantity/value2/upper|unit", "1")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatCompositionMap),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }
}
