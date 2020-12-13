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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class TermMappingTest extends AbstractWebTemplateTest {

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
    public void testTermMappingToObjectMap() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition.json"));

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0|match", "="));
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|terminology", "SNOMED-CT"));
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|code", "21794005"));
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1|match", "="));
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|terminology", "RTX"));
        assertThat(flatComposition).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|code", "W.11.7"));

    }

    @Test
    public void sendMapping() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.64")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0|match", "=")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|terminology", "SNOMED-CT")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|code", "99302")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1|match", "=")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|terminology", "RTX")
                .put("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|code", "XYZ").build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "CA",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "User"),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.64"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|terminology", "local"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|value", "Chills / rigor / shivering"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0|match", "="));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|terminology", "SNOMED-CT"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|code", "99302"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1|match", "="));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|terminology", "RTX"));
        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|code", "XYZ"));
    }

    @Test
    public void sendRetrieveMappingOnDvText() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name|code", "117")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name|terminology", "mine")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name|value", "Hello world!")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:0|match", "=")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:0/target|terminology", "SNOMED-CT")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:0/target|code", "99302")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:1|match", "=")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:1/target|terminology", "RTX")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name/_mapping:1/target|code", "XYZ").build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "CA",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "User"),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name|code", "117"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name|terminology", "mine"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name|value", "Hello world!"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:0|match", "="));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:0/target|terminology", "SNOMED-CT"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:0/target|code", "99302"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:1|match", "="));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:1/target|terminology", "RTX"));
        assertThat(retrieved).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_mapping:1/target|code", "XYZ"));
    }

    @Test
    public void testTermMappingToStringMap() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition.json"));

        Map<String, Object> stringMap = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0|match", "="));
        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|terminology", "SNOMED-CT"));
        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:0/target|code", "21794005"));
        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1|match", "="));
        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|terminology", "RTX"));
        assertThat(stringMap).contains(entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0/_mapping:1/target|code", "W.11.7"));
    }

    @Test
    public void testTermMappingToJson() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        
        assertThat(structuredComposition).isNotNull();
        JsonNode mappingNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0).path("any_event").path(0).path("symptoms").path(0)
                .path("_mapping");
        assertThat(mappingNode.isNull()).isFalse();
        assertThat(mappingNode.isArray()).isTrue();
        assertThat(mappingNode.size()).isEqualTo(2);
        assertThat(mappingNode.path(0).path("|match").asText()).isEqualTo("=");
        assertThat(mappingNode.path(0).path("target").path(0).path("|terminology").asText()).isEqualTo("SNOMED-CT");
        assertThat(mappingNode.path(0).path("target").path(0).path("|code").asText()).isEqualTo("21794005");
        assertThat(mappingNode.path(1).path("|match").asText()).isEqualTo("=");
        assertThat(mappingNode.path(1).path("target").path(0).path("|terminology").asText()).isEqualTo("RTX");
        assertThat(mappingNode.path(1).path("target").path(0).path("|code").asText()).isEqualTo("W.11.7");

        JsonNode dvTextNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0).path("any_event").path(0).path(
                "description_of_thermal_stress");
        assertThat(dvTextNode.isArray()).isTrue();
        assertThat(dvTextNode.size()).isEqualTo(1);
        assertThat(dvTextNode.path(0).isObject()).isTrue();
        assertThat(dvTextNode.path(0).path("|value").asText()).isEqualTo("Test description of symptoms Modified With Term. Mapping");
        JsonNode dvTextMn = dvTextNode.path(0).path("_mapping");
        assertThat(dvTextMn.isArray()).isTrue();
        assertThat(dvTextMn.path(0).path("|match").asText()).isEqualTo("=");
        assertThat(dvTextMn.path(0).path("target").path(0).isObject()).isTrue();
        assertThat(dvTextMn.path(0).path("target").path(0).path("|terminology").asText()).isEqualTo("IAXA");
        assertThat(dvTextMn.path(0).path("target").path(0).path("|code").asText()).isEqualTo("99.1");
        assertThat(dvTextMn.path(0).path("purpose").path(0).isObject()).isTrue();
        assertThat(dvTextMn.path(0).path("purpose").path(0).path("|terminology").asText()).isEqualTo("Purposes");
        assertThat(dvTextMn.path(0).path("purpose").path(0).path("|code").asText()).isEqualTo("p.0.63.1");
        assertThat(dvTextMn.path(0).path("purpose").path(0).path("|value").asText()).isEqualTo("Purpose 1");
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testTermMappingFromJson() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        
        assertThat(structuredComposition).isNotNull();
        JsonNode mappingNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0).path("any_event").path(0).path("symptoms").path(0)
                .path("_mapping");
        assertThat(mappingNode.isNull()).isFalse();

        JsonNode retrievedRawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test_composer"),
                objectMapper
        );

        JsonNode termMappings = retrievedRawComposition.get("content").get(0).get("items").get(1).get("data").get("events").get(0).get("data").get("items").get(
                1).get("value").get("mappings");

        assertThat(termMappings).hasSize(2);

        assertThat(termMappings.get(0).get("match").asText()).isEqualTo("=");
        assertThat(termMappings.get(0).get("target").get("code_string").asText()).isEqualTo("21794005");
        assertThat(termMappings.get(0).get("target").get("terminology_id").get("value").asText()).isEqualTo("SNOMED-CT");
        assertThat(termMappings.get(0).get("purpose")).isInstanceOf(NullNode.class);
        assertThat(termMappings.get(1).get("match").asText()).isEqualTo("=");
        assertThat(termMappings.get(1).get("target").get("code_string").asText()).isEqualTo("W.11.7");
        assertThat(termMappings.get(1).get("target").get("terminology_id").get("value").asText()).isEqualTo("RTX");
        assertThat(termMappings.get(1).get("purpose")).isInstanceOf(NullNode.class);


        termMappings = retrievedRawComposition.get("content").get(0).get("items").get(1).get("data").get("events").get(0).get("state").get("items").get(0).get("value").get(
                "mappings");

        assertThat(termMappings).hasSize(1);
        assertThat(termMappings.get(0).get("match").asText()).isEqualTo("=");
        assertThat(termMappings.get(0).get("target").get("code_string").asText()).isEqualTo("99.1");
        assertThat(termMappings.get(0).get("target").get("terminology_id").get("value").asText()).isEqualTo("IAXA");
        assertThat(termMappings.get(0).get("purpose")).isNotNull();
        assertThat(termMappings.get(0).get("purpose").get("defining_code").get("code_string").asText()).isEqualTo("p.0.63.1");
        assertThat(termMappings.get(0).get("purpose").get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("Purposes");
        assertThat(termMappings.get(0).get("purpose").get("value").asText()).isEqualTo("Purpose 1");
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testTermMappingFromJsonNoPurposeValue() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition2.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(structuredComposition).isNotNull();
        JsonNode mappingNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0).path("any_event").path(0).path("symptoms").path(0)
                .path("_mapping");
        assertThat(mappingNode.isNull()).isFalse();

        assertThatThrownBy(() -> getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test_composer"),
                objectMapper
        )).isInstanceOf(Exception.class);
    }

    @SuppressWarnings("ReuseOfLocalVariable")
    @Test
    public void testTermMappingFromMap() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/TmComposition.json"));

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        JsonNode retrievedRawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test_composer"),
                objectMapper
        );

        JsonNode termMappings = retrievedRawComposition.get("content").get(0).get("items").get(1).get("data").get("events").get(0).get("data").get("items").get(
                1).get("value").get("mappings");
        assertThat(termMappings).hasSize(2);
        
        JsonNode tm = termMappings.get(0);
        assertThat(tm.get("match").asText()).isEqualTo("=");
        assertThat(tm.get("target").get("code_string").asText()).isEqualTo("21794005");
        assertThat(tm.get("target").get("terminology_id").get("value").asText()).isEqualTo("SNOMED-CT");
        assertThat(tm.get("purpose")).isInstanceOf(NullNode.class);
        tm = termMappings.get(1);
        assertThat(tm.get("match").asText()).isEqualTo("=");
        assertThat(tm.get("target").get("code_string").asText()).isEqualTo("W.11.7");
        assertThat(tm.get("target").get("terminology_id").get("value").asText()).isEqualTo("RTX");
        assertThat(tm.get("purpose")).isInstanceOf(NullNode.class);


        termMappings = retrievedRawComposition.get("content").get(0).get("items").get(1).get("data").get("events").get(0).get("state").get("items").get(0).get("value").get("mappings");
        assertThat(termMappings).hasSize(1);
        tm = termMappings.get(0);
        assertThat(tm.get("match").asText()).isEqualTo("=");
        assertThat(tm.get("target").get("code_string").asText()).isEqualTo("99.1");
        assertThat(tm.get("target").get("terminology_id").get("value").asText()).isEqualTo("IAXA");
        assertThat(tm.get("purpose")).isNotNull();
        assertThat(tm.get("purpose").get("defining_code").get("code_string").asText()).isEqualTo("p.0.63.1");
        assertThat(tm.get("purpose").get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("Purposes");
        assertThat(tm.get("purpose").get("value").asText()).isEqualTo("Purpose 1");
    }
}
