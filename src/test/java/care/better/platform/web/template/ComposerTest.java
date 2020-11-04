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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WebTemplateTestExtension.class)
public class ComposerTest extends AbstractWebTemplateTest {

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
    public void composer() throws Exception {
        String template = getFileContent("/Demo Vitals.opt");

        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("ctx/composer_id", "1191")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .build();

        String flatComposition = objectMapper.writeValueAsString(flatCompositionMap);

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                flatComposition,
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isInstanceOf(ObjectNode.class);
        assertThat(rawComposition.get("composer").get("external_ref").get("id").get("value").asText()).isEqualTo("1191");
    }

    @Test
    public void composerSelf() throws Exception {
        String template = getFileContent("/Demo Vitals.opt");

        ImmutableMap<String, String> flatCompositionMap = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_self", "true")
                .put("ctx/composer_id", "1191")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .build();

        String flatComposition = objectMapper.writeValueAsString(flatCompositionMap);

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                flatComposition,
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isInstanceOf(ObjectNode.class);
        assertThat(rawComposition.get("composer").get("external_ref").get("id").get("value").asText()).isEqualTo("1191");
    }
}
