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

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class EthercisBuilderTest extends AbstractWebTemplateTest {

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
    public void partyIdentifiedWithHierObjectIdTest() throws Exception {
        String template = getFileContent("/res/action test.opt");
        String rawCompositionString = getFileContent("/res/action_test.json");

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawCompositionString,
                objectMapper);

        assertThat(flatComposition).isNotEmpty();
    }

    @Test
    public void ctxValuesTest() throws Exception {
        String template = getFileContent("/res/RIPPLE-ConformanceTesttemplate.opt");
        String rawCompositionString = getFileContent("/res/conformance test.json");

        JsonNode rawComposition = objectMapper.readTree(rawCompositionString);

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition1.get("context").get("start_time").get("value").asText())
                .isEqualTo(rawComposition.get("context").get("start_time").get("value").asText());
        assertThat(rawComposition1.get("language").get("code_string").asText()).isEqualTo(rawComposition.get("language").get("code_string").asText());
        assertThat(rawComposition1.get("territory").get("code_string").asText()).isEqualTo(rawComposition.get("territory").get("code_string").asText());
        assertThat(getCompositionValidator().validate(template, rawComposition1.toString())).hasSize(8);
    }
}
