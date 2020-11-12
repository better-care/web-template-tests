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

import static org.assertj.core.api.Assertions.*;

/**
 * @author Marko Narat
 */
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
@ExtendWith(WebTemplateTestExtension.class)
public class OtherReferenceRangesTest extends AbstractWebTemplateTest {

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
    public void otherReferenceRangesSingle() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(
                getFileContent("/res/ReferenceRangesSingle.json"),
                new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrievedFlatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrievedFlatComposition).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|magnitude",
                      2.5d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|magnitude",
                      6.6d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/meaning",
                      "too high"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/lower|magnitude",
                      6.6d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/upper|magnitude",
                      15.1d));
    }

    @Test
    public void otherReferenceRangesInvalidAttribute() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/ReferenceRangesInvalid.json"), new TypeReference<Map<String, Object>>() {});

        assertThatThrownBy(() -> {
            getCompositionConverter().convertFlatToRaw(
                    template,
                    "en",
                    objectMapper.writeValueAsString(flatComposition),
                    Collections.emptyMap(),
                    objectMapper);
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void otherReferenceRangesMulti() throws Exception {
        String template = getFileContent("/res/IDCR_-_Laboratory_Test_Report.v0.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/ReferenceRangesMulti.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrievedFlatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrievedFlatComposition).contains(
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|magnitude",
                      2.5d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|magnitude",
                      6.6d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_normal_range/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/meaning",
                      "too high"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/lower|magnitude",
                      6.6d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/upper|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:0/result_value/_other_reference_ranges:0/upper|magnitude",
                      15.1d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:0/meaning|code",
                      "X"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:0/meaning|terminology",
                      "mine"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:0/meaning|value",
                      "too high"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:0/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:0/lower|magnitude",
                      110.0d),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:1/meaning|code",
                      "Y"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:1/meaning|terminology",
                      "mine"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:1/meaning|value",
                      "much too high"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:1/lower|unit",
                      "mmol/l"),
                entry("laboratory_test_report/laboratory_test:0/laboratory_test_panel/laboratory_result:1/result_value/_other_reference_ranges:1/lower|magnitude",
                      150.0d)
        );
    }
}
