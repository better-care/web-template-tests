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
import com.fasterxml.jackson.databind.node.ObjectNode;
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
public class SubjectTest extends AbstractWebTemplateTest {

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
    public void entrySubject() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved.keySet()).doesNotContain("vitals/vitals/haemoglobin_a1c:0/subject|name", "vitals/vitals/haemoglobin_a1c:0/subject|id");

        JsonNode node = rawComposition.get("content").get(0).get("items").get(0);

        assertThat(node.get("subject").get("@class").asText()).isEqualTo("PARTY_SELF");
    }

    @Test
    public void constrainedEntrySubject() throws Exception {
        String template = getFileContent("/res/Test constrained subject.opt");

        ImmutableMap<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("test_constrained_subject/maternal_pregnancy:0/maternal_age", "P25Y")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved.keySet()).doesNotContain("test_constrained_subject/maternal_pregnancy:0/subject|name",
                                                      "test_constrained_subject/maternal_pregnancy:0/subject|id");
        assertThat(rawComposition.get("content").get(0).get("subject").get("@class").asText()).isEqualTo("PARTY_RELATED");
    }

    @Test
    public void customEntrySubject() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .put("vitals/vitals/haemoglobin_a1c/subject|name", "Marija Medved")
                        .put("vitals/vitals/haemoglobin_a1c/subject|id", "998")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/subject|name", "Marija Medved"),
                entry("vitals/vitals/haemoglobin_a1c:0/subject|id", "998"));

        retrieved.put("ctx/language", "sl");
        retrieved.put("ctx/territory", "SI");
        retrieved.put("ctx/id_scheme", "ispek");
        retrieved.put("ctx/id_namespace", "ispek");
        retrieved.put("ctx/composer_name", "George Orwell");

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(retrieved),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition1.get("content").get(0).get("items").get(0).get("subject").get("external_ref").get("id").get("value").asText()).isEqualTo("998");
    }

    @Test
    public void nullEntrySubject() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        ObjectNode node = (ObjectNode)rawComposition.get("content").get(0).get("items").get(0);
        node.set("subject", null);

        Map<String, Object> retrieved1 = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved1.keySet()).doesNotContain("vitals/vitals/haemoglobin_a1c:0/subject|name", "vitals/vitals/haemoglobin_a1c:0/subject|id");
        node.set("subject", objectMapper.readTree("{\"@class\":\"PARTY_IDENTIFIED\",\"external_ref\":null,\"name\":null,\"identifiers\":[]}"));

        Map<String, Object> retrieved2 = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved2.keySet()).doesNotContain("vitals/vitals/haemoglobin_a1c:0/subject|name", "vitals/vitals/haemoglobin_a1c:0/subject|id");
    }

    @Test
    public void retrievedTest() throws Exception {
        String template = getFileContent("/res/clinical-summary-events.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Subject.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }
}
