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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
@ExtendWith(WebTemplateTestExtension.class)
public class NamedElementsTest extends AbstractWebTemplateTest {

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
    public void customNames() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NamedElements1.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|value", "Urea"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|code", "365755003"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|terminology",
                      "SNOMED-CT"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|value", "Creatinine"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|code", "70901006"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|terminology",
                      "SNOMED-CT"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_name|value", "Sodium"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_name|code", "365761000"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_name|terminology",
                      "SNOMED-CT"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_name|value", "Potassium"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_name|code", "365760004"));
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_name|terminology",
                      "SNOMED-CT"));
    }

    @Test
    public void customNamesAsDvText() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NamedElements2.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name", "Urea"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name", "New name"));
        assertThat(retrieve.keySet()).doesNotContain(
                "laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|code",
                "laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|terminology",
                "laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|code",
                "laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|terminology");
    }

    @Test
    public void regularNames() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NamedElements3.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(
                retrieve.containsKey("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_name|value")).isFalse();
        assertThat(retrieve.containsKey("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value|magnitude")).isTrue();
        assertThat(
                retrieve.containsKey("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_name|value")).isFalse();
        assertThat(retrieve.containsKey("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value|magnitude")).isTrue();
        assertThat(
                retrieve.containsKey("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:0/result_value/_name|value")).isFalse();
        assertThat(retrieve.containsKey("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:0/result_value|magnitude")).isTrue();
        assertThat(
                retrieve.containsKey("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:1/result_value/_name|value")).isFalse();
        assertThat(retrieve.containsKey("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:1/result_value|magnitude")).isTrue();
        assertThat(retrieve).doesNotContain(
                entry("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:1/result_value/_name|value", "Potassium"),
                entry("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:1/result_value/_name|code", "365760004"),
                entry("laboratory_test_report/laboratory_test:1/laboratory_test_panel/laboratory_result:1/result_value/_name|terminology",
                      "SNOMED-CT"));

        assertThat(retrieve.keySet().stream().anyMatch(input -> input.contains("/_name"))).isFalse();
    }
}