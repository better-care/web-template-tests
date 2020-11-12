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
public class NormalRangeTest extends AbstractWebTemplateTest {

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
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NormalRange.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrieve).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|magnitude",
                      2.5d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|magnitude",
                      6.6d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_normal_range/lower|magnitude",
                      80.0d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_normal_range/upper|magnitude",
                      110.0d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_normal_range/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_normal_range/lower|magnitude",
                      133.0d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_normal_range/upper|magnitude",
                      146.0d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:2/result_value/_normal_range/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_normal_range/lower|magnitude",
                      3.5d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_normal_range/upper|magnitude",
                      5.3d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:3/result_value/_normal_range/upper|unit",
                      "mmol/l"));
    }
}
