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
public class FeederAuditTest extends AbstractWebTemplateTest {

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
    public void wtToComposition() throws Exception {
        JsonNode rawComposition = buildComposition();
        validateFeederAudit(rawComposition);
    }

    @Test
    public void wtToCompositionDeep() throws Exception {
        JsonNode rawComposition = buildDeepComposition();
        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void wtToCompositionMultimedia() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = buildCompositionWithMultiMedia();
        assertThat(rawComposition).isNotNull();

        JsonNode feederAudit = getFeederAudit(rawComposition);
        assertThat(feederAudit.get("original_content").get("@class").asText()).isEqualTo("DV_MULTIMEDIA");
        JsonNode originalContent = feederAudit.get("original_content");
        assertThat(originalContent.get("media_type").get("code_string").asText()).isEqualTo("text/html");
        assertThat(originalContent.get("uri").get("value").asText()).isEqualTo("http://www.marand.com");

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(flatComposition).contains(
                entry("vitals/vitals/body_temperature:0/_feeder_audit/original_content_multimedia", "http://www.marand.com"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/original_content_multimedia|mediatype", "text/html"));
    }

    @Test
    public void compositionToWtDeep() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = buildDeepComposition();
        assertThat(rawComposition).isNotNull();
        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(flatComposition).contains(
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id", "123"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|name", "John Smith"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id_scheme", "seq"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id_namespace", "kzz"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|time", "2017-01-31T00:00:00Z"));
    }

    @Test
    public void compositionToWt() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = buildComposition();
        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(flatComposition).contains(
                entry("vitals/vitals/body_temperature:0/_feeder_audit/original_content", "Hello world!"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/original_content|formalism", "text/plain"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|assigner", "assigner1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|issuer", "issuer1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0", "id1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|type", "PERSON"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|assigner", "assigner2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|issuer", "issuer2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1", "id2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|type", "PERSON"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|assigner", "assigner1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|issuer", "issuer1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0", "id1"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|type", "PERSON"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|assigner", "assigner2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|issuer", "issuer2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1", "id2"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|type", "PERSON"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|system_id", "orig"),
                entry("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|version_id", "vvv")
        );
    }

    @Test
    public void structuredWithFeederAudit() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/DemoVitalsCompositionFeederAudit.json"));
        Map<String, Object> flatComposition = getCompositionConverter().convertStructuredToFlat(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(flatComposition).contains(
                entry("vitals/_feeder_audit/original_content", "{\"hello\": \"world\"}"),
                entry("vitals/_feeder_audit/original_content|formalism", "application/json"),
                entry("vitals/_feeder_audit/originating_system_audit|system_id", "mine"));
    }

    @Test
    public void structuredWithGenericFields() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/DemoVitalsCompositionGenericFields.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition.get("feeder_audit")).isNotNull();
        assertThat(rawComposition.get("feeder_audit").get("originating_system_audit")).isNotNull();
        assertThat(rawComposition.get("feeder_audit").get("originating_system_audit").get("system_id").asText()).isEqualTo("FormRenderer");

        JsonNode retrievedStructuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").isObject()).isTrue();
        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").path("field1").isArray()).isTrue();
        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").path("field1").path(0).textValue()).isEqualTo("val1");
        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").path("field1").path(1).textValue()).isEqualTo("val2");
        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").path("field2").path(0).textValue()).isEqualTo("val3");
        assertThat(retrievedStructuredComposition.path("ctx").path("generic_fields").path("field2").path(1).textValue()).isEqualTo("val4");
    }

    @Test
    public void feederAuditBroken() throws Exception {
        String flatComposition = getFileContent("/res/gel_data.json");
        String template = getFileContent("/res/GEL Cancer diagnosis input.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                flatComposition,
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        assertThat(rawComposition.get("content").get(0).get("feeder_audit")).isNotNull();
        assertThat(rawComposition.get("content").get(0).get("feeder_audit").get("originating_system_audit").get("system_id").asText()).isEqualTo("infoflex");
        assertThat(rawComposition.get("content").get(0).get("feeder_audit").get("originating_system_audit").get("time")
                           .get("value").asText()).isEqualTo("2018-01-01T03:00:00Z");

    }


    private JsonNode buildDeepComposition() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id", "123")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|name", "John Smith")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id_scheme", "seq")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit/location|id_namespace", "kzz")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|time", "2017-01-31T00:00:00Z")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 34.1)
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.65")
                .put("vitals/vitals/body_temperature:0/any_event:0/body_exposure|code", "at0033")
                .put("vitals/vitals/body_temperature:0/any_event:0/description_of_thermal_stress", "Description of thermal stress 73")
                .build();

        return getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI"),
                objectMapper);
    }

    private JsonNode buildCompositionWithMultiMedia() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("vitals/vitals/body_temperature:0/_feeder_audit/original_content_multimedia|url", "http://www.marand.com")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/original_content_multimedia|mediatype", "text/html")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 34.1)
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.65")
                .put("vitals/vitals/body_temperature:0/any_event:0/body_exposure|code", "at0033")
                .put("vitals/vitals/body_temperature:0/any_event:0/description_of_thermal_stress", "Description of thermal stress 73")
                .build();

        return getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI"),
                objectMapper);
    }

    private JsonNode buildComposition() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("vitals/vitals/body_temperature:0/_feeder_audit/original_content", "Hello world!")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/original_content|formalism", "text/plain")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|assigner", "assigner1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|issuer", "issuer1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|id", "id1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:0|type", "PERSON")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|assigner", "assigner2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|issuer", "issuer2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|id", "id2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_item_id:1|type", "PERSON")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|assigner", "assigner1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|issuer", "issuer1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|id", "id1")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:0|type", "PERSON")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|assigner", "assigner2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|issuer", "issuer2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|id", "id2")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/feeder_system_item_id:1|type", "PERSON")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|system_id", "orig")
                .put("vitals/vitals/body_temperature:0/_feeder_audit/originating_system_audit|version_id", "vvv")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 34.1)
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.65")
                .put("vitals/vitals/body_temperature:0/any_event:0/body_exposure|code", "at0033")
                .put("vitals/vitals/body_temperature:0/any_event:0/description_of_thermal_stress", "Description of thermal stress 73")
                .build();

        return getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI"),
                objectMapper);
    }

    private void validateFeederAudit(JsonNode rawComposition) {
        assertThat(rawComposition).isNotNull();

        JsonNode feederAudit = getFeederAudit(rawComposition);
        assertThat(feederAudit).isNotNull();

        assertThat(feederAudit.get("original_content").get("@class").asText()).isEqualTo("DV_PARSABLE");

        JsonNode originalContent = feederAudit.get("original_content");
        assertThat(originalContent.get("value").asText()).isEqualTo("Hello world!");
        assertThat(originalContent.get("formalism").asText()).isEqualTo("text/plain");
        JsonNode originatingSystemItemIds = feederAudit.get("originating_system_item_ids");
        assertThat(originatingSystemItemIds).hasSize(2);
        assertThat(originatingSystemItemIds.get(0).get("assigner").asText()).isEqualTo("assigner1");
        assertThat(originatingSystemItemIds.get(1).get("assigner").asText()).isEqualTo("assigner2");
        assertThat(originatingSystemItemIds.get(0).get("issuer").asText()).isEqualTo("issuer1");
        assertThat(originatingSystemItemIds.get(1).get("issuer").asText()).isEqualTo("issuer2");
        assertThat(originatingSystemItemIds.get(0).get("id").asText()).isEqualTo("id1");
        assertThat(originatingSystemItemIds.get(1).get("id").asText()).isEqualTo("id2");
        assertThat(originatingSystemItemIds.get(0).get("type").asText()).isEqualTo("PERSON");
        assertThat(originatingSystemItemIds.get(1).get("type").asText()).isEqualTo("PERSON");

        assertThat(feederAudit.get("feeder_system_item_ids")).hasSize(2);
        assertThat(feederAudit.get("feeder_system_item_ids").get(0).get("assigner").asText()).isEqualTo("assigner1");
        assertThat(feederAudit.get("feeder_system_item_ids").get(1).get("assigner").asText()).isEqualTo("assigner2");
        assertThat(feederAudit.get("feeder_system_item_ids").get(0).get("issuer").asText()).isEqualTo("issuer1");
        assertThat(feederAudit.get("feeder_system_item_ids").get(1).get("issuer").asText()).isEqualTo("issuer2");
        assertThat(feederAudit.get("feeder_system_item_ids").get(0).get("id").asText()).isEqualTo("id1");
        assertThat(feederAudit.get("feeder_system_item_ids").get(1).get("id").asText()).isEqualTo("id2");
        assertThat(feederAudit.get("feeder_system_item_ids").get(0).get("type").asText()).isEqualTo("PERSON");
        assertThat(feederAudit.get("feeder_system_item_ids").get(1).get("type").asText()).isEqualTo("PERSON");

        assertThat(feederAudit.get("originating_system_audit").get("system_id").asText()).isEqualTo("orig");
        assertThat(feederAudit.get("originating_system_audit").get("version_id").asText()).isEqualTo("vvv");
    }

    private JsonNode getFeederAudit(JsonNode rawComposition) {
        JsonNode section = rawComposition.get("content").get(0);
        JsonNode observation = section.get("items").get(0);
        return observation.get("feeder_audit");
    }
}
