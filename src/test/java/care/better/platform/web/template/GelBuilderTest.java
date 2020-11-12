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

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class GelBuilderTest extends AbstractWebTemplateTest {

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
    public void missingNameMapping() throws Exception {

        String template = getFileContent("/res/GEL_-_Generic_Lab_Report_import.v0.opt");
        String flatCompositionString = getFileContent("/res/GenericLabReport.json");

        JsonNode structuredComposition = getCompositionConverter().convertFlatToStructured(
                template,
                "sl",
                flatCompositionString,
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> flatComposition = getCompositionConverter().convertStructuredToFlat(
                template,
                "sl",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(flatComposition).contains(
                entry("laboratory_result_report/laboratory_test:0/laboratory_test_panel:0/laboratory_result:0/result_value/_name/_mapping:0/target|code",
                      "5195-3"));
    }
}
