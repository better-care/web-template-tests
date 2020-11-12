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
import com.google.common.collect.ImmutableMap;
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
public class LinksTest extends AbstractWebTemplateTest {

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
    public void links() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .put("vitals/vitals/haemoglobin_a1c/_link:0|meaning", "none")
                .put("vitals/vitals/haemoglobin_a1c/_link:0|type", "href")
                .put("vitals/vitals/haemoglobin_a1c/_link:0|target", "http://www.sun.com")
                .put("vitals/vitals/haemoglobin_a1c/_link:1|meaning", "serious")
                .put("vitals/vitals/haemoglobin_a1c/_link:1|type", "url")
                .put("vitals/vitals/haemoglobin_a1c/_link:1|target", "http://www.ehrscape.com")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrievedComposition = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrievedComposition).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|meaning", "none"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|type", "href"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|target", "http://www.sun.com"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|meaning", "serious"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|type", "url"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|target", "http://www.ehrscape.com")
        );
    }

    @Test
    public void linksFromCtx() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/link|type", "EHR")
                .put("ctx/link|meaning", "link")
                .put("ctx/link|target", "ehr://uid/value")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrievedComposition = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrievedComposition).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|meaning", "link"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|type", "EHR"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|target", "ehr://uid/value")
        );
    }

    @Test
    public void linksFromCtxMulti() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/link:17|type", "EHR1")
                .put("ctx/link:17|meaning", "link1")
                .put("ctx/link:17|target", "ehr://uid/value1")
                .put("ctx/link:99|type", "EHR2")
                .put("ctx/link:99|meaning", "link2")
                .put("ctx/link:99|target", "ehr://uid/value2")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrievedComposition = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrievedComposition).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|meaning", "link1"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|type", "EHR1"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:0|target", "ehr://uid/value1"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|meaning", "link2"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|type", "EHR2"),
                entry("vitals/vitals/haemoglobin_a1c:0/_link:1|target", "ehr://uid/value2")
        );
    }
}
