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
public class ProviderTest extends AbstractWebTemplateTest {

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
    public void entryProvider() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .put("vitals/vitals/haemoglobin_a1c/_provider|name", "Marija Medved")
                .put("vitals/vitals/haemoglobin_a1c/_provider|id", "998")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieved).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/_provider|name", "Marija Medved"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider|id", "998")
        );

        retrieved.put("ctx/language", "sl");
        retrieved.put("ctx/territory", "SI");
        retrieved.put("ctx/id_scheme", "ispek");
        retrieved.put("ctx/id_namespace", "ispek");
        retrieved.put("ctx/composer_name", "George Orwell");

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(rawComposition1
                           .get("content").get(0)
                           .get("items").get(0)
                           .get("provider")
                           .get("external_ref")
                           .get("id")
                           .get("value").asText()).isEqualTo("998");
    }

    @Test
    public void entryProviderWithIdentifiers() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .put("vitals/vitals/haemoglobin_a1c/_provider|name", "Marija Medved")
                .put("vitals/vitals/haemoglobin_a1c/_provider|id", "998")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:0", "1")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:0|type", "person")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:0|assigner", "nhs")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:0|issuer", "nhs")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:1", "123")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:1|type", "person")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:1|assigner", "uk")
                .put("vitals/vitals/haemoglobin_a1c/_provider/_identifier:1|issuer", "uk")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieved).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/_provider|name", "Marija Medved"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider|id", "998"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:0", "1"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:0|type", "person"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:0|assigner", "nhs"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:0|issuer", "nhs"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:1", "123"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:1|type", "person"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:1|assigner", "uk"),
                entry("vitals/vitals/haemoglobin_a1c:0/_provider/_identifier:1|issuer", "uk")
        );

        retrieved.put("ctx/language", "sl");
        retrieved.put("ctx/territory", "SI");
        retrieved.put("ctx/id_scheme", "ispek");
        retrieved.put("ctx/id_namespace", "ispek");
        retrieved.put("ctx/composer_name", "George Orwell");

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(rawComposition1
                           .get("content").get(0)
                           .get("items").get(0)
                           .get("provider")
                           .get("external_ref")
                           .get("id")
                           .get("value").asText()).isEqualTo("998");
    }

    @Test
    public void healthCareFacility() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        ImmutableMap<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("vitals/context/_health_care_facility|name", "Hospital")
                .put("vitals/context/_health_care_facility/_identifier:0", "17")
                .put("vitals/context/_health_care_facility/_identifier:0|assigner", "uk")
                .put("vitals/context/_health_care_facility/_identifier:0|issuer", "uk")
                .put("vitals/context/_health_care_facility/_identifier:0|type", "ESTABLISHMENT")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieved).contains(
                entry("vitals/context/_health_care_facility|name", "Hospital"),
                entry("vitals/context/_health_care_facility/_identifier:0", "17"),
                entry("vitals/context/_health_care_facility/_identifier:0|type", "ESTABLISHMENT"),
                entry("vitals/context/_health_care_facility/_identifier:0|assigner", "uk"),
                entry("vitals/context/_health_care_facility/_identifier:0|issuer", "uk")
        );
    }
}
