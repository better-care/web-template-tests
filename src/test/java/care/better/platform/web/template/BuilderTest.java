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

import care.better.platform.web.template.extension.WebTemplateTestExtension;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class BuilderTest extends AbstractWebTemplateTest {

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
    public void childGrowthTest() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Child Growth Encounter.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/Child Growth.json"));
        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void vitalFunctionsTest() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Vital Functions Encounter.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/Vital Functions.json"));
        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void assessmentScalesTest() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Assessment Scales Encounter.opt");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/Assessment Scales.json"));
        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void medicationsTest() throws Exception {
        String template = getFileContent("/res/medication_list.opt");
        String rawCompositionString = getFileContent("/res/Medications.json");
        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "sv", rawCompositionString, objectMapper);
        assertThat(flatComposition).isNotNull();
    }

    @Test
    public void vitalsTest() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");
        String rawCompositionString = getFileContent("/res/Demo Vitals.json");
        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "sl", rawCompositionString, objectMapper);
        assertThat(flatComposition).isNotNull();
    }
}
