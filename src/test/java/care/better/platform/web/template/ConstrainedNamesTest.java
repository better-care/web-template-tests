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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(WebTemplateTestExtension.class)
public class ConstrainedNamesTest extends AbstractWebTemplateTest {

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
    public void relaxedNamesConversion() throws Exception {
        String template = getFileContent("/openEHR-EHR-COMPOSITION.t_specialist_examination.opt");
        String rawComposition = getFileContent("/ru-compositionWithRelaxedNames.json");

        Map<String, Object> flatCompostion = getCompositionConverter().convertRawToFlat(template, "ru", rawComposition, objectMapper);
        assertThat(flatCompostion).containsKeys(
                "осмотр_специалиста/диагноз/сопутствующие_заболевания/сопутствующее_заболевание:0/код_по_мкб_10|code",
                "осмотр_специалиста/диагноз/сопутствующие_заболевания/сопутствующее_заболевание:1/код_по_мкб_10|code"
        );
    }

    @Test
    public void namesWithTerminologies() throws Exception {
        String template = getFileContent("/Laboratory report.xml");

        String rawComposition = getFileContent("/namesExtTerminology.json");
        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition, objectMapper);

        assertThat(flatComposition).contains(
                entry("report/laboratory_test_result:0/_name|value", "LipidPanel"),
                entry("report/laboratory_test_result:0/_name|code", "3219"),
                entry("report/laboratory_test_result:0/_name|terminology", "LOINC"),
                entry("report/laboratory_test_result:0/any_event:0/laboratory_test_analyte:0/_name|value", "HDL"),
                entry("report/laboratory_test_result:0/any_event:0/laboratory_test_analyte:0/_name|code", "3218"),
                entry("report/laboratory_test_result:0/any_event:0/laboratory_test_analyte:0/_name|terminology", "LOINC")
        );
    }

}
