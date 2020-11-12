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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
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
@SuppressWarnings("AnonymousInnerClassMayBeStatic")
@ExtendWith(WebTemplateTestExtension.class)
public class NullFlavorTest extends AbstractWebTemplateTest {

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
    public void nullFlavourJsonRetrieve() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/NullFlavor1.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();

        ObjectNode elementNode = (ObjectNode)rawComposition.get("content").get(0).get("items").get(0).get("data").get("events").get(0).get("data")
                .get("items").get(0);
        ObjectNode codedTextNode = objectMapper.createObjectNode();
        codedTextNode.put("@class", "DV_CODED_TEXT");
        codedTextNode.put("value", "not applicable");

        ObjectNode codePhraseNode = objectMapper.createObjectNode();
        codePhraseNode.put("@class", "CODE_PHRASE");
        codePhraseNode.put("code_string", "273");

        ObjectNode terminologyNode = objectMapper.createObjectNode();
        terminologyNode.put("@class", "TERMINOLOGY_ID");
        terminologyNode.put("value", "openehr");

        codePhraseNode.set("terminology_id", terminologyNode);
        codedTextNode.set("defining_code", codePhraseNode);

        elementNode.putNull("value");
        elementNode.set("null_flavour", codedTextNode);

        JsonNode retrievedStructured = getCompositionConverter().convertRawToStructured(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrievedStructured.isNull()).isFalse();
        assertThat(retrievedStructured.get("vitals").get("vitals").isArray()).isTrue();
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").isArray()).isTrue();
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").isArray()).isTrue();
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").get(0).isObject()).isTrue();
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").get(0).get("_null_flavour").isArray()).isTrue();
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").get(0).get("_null_flavour").get(0).get("|code").asText()).isEqualTo("273");
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").get(0).get("_null_flavour").get(0).get("|value").asText()).isEqualTo("not applicable");
        assertThat(retrievedStructured.get("vitals").get("vitals").get(0).get("haemoglobin_a1c").get(0).get("any_event")
                           .get(0).get("test_name").get(0).get("_null_flavour").get(0).get("|terminology").asText()).isEqualTo("openehr");
    }

    @Test
    public void nullFlavourPlainRetrieve() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/NullFlavor2.json"));
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();

        ObjectNode elementNode = (ObjectNode)rawComposition.get("content").get(0).get("items").get(0).get("data").get("events").get(0).get("data")
                .get("items").get(0);
        ObjectNode codedTextNode = objectMapper.createObjectNode();
        codedTextNode.put("@class", "DV_CODED_TEXT");
        codedTextNode.put("value", "not applicable");

        ObjectNode codePhraseNode = objectMapper.createObjectNode();
        codePhraseNode.put("@class", "CODE_PHRASE");
        codePhraseNode.put("code_string", "273");

        ObjectNode terminologyNode = objectMapper.createObjectNode();
        terminologyNode.put("@class", "TERMINOLOGY_ID");
        terminologyNode.put("value", "openehr");

        codePhraseNode.set("terminology_id", terminologyNode);
        codedTextNode.set("defining_code", codePhraseNode);

        elementNode.putNull("value");
        elementNode.set("null_flavour", codedTextNode);


        Map<String, Object> retrievedFlat = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(retrievedFlat).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_null_flavour|code", "273"));
        assertThat(retrievedFlat).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_null_flavour|value", "not applicable"));
        assertThat(retrievedFlat).contains(entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_name/_null_flavour|terminology", "openehr"));
    }

    @Test
    public void nullFlavourJsonBuild() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/NullFlavor3.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();

        JsonNode nullFlavour = rawComposition.get("content").get(0).get("items").get(0).get("data").get("events")
                .get(0).get("data").get("items").get(0).get("null_flavour");
        assertThat(nullFlavour).isNotNull();
        assertThat(nullFlavour.get("defining_code").get("code_string").asText()).isEqualTo("273");
        assertThat(nullFlavour.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(nullFlavour.get("value").asText()).isEqualTo("not applicable");
        assertThat(rawComposition.get("content").get(0).get("items").get(0).get("data").get("events")
                           .get(0).get("data").get("items").get(0).get("value")).isInstanceOf(NullNode.class);

        JsonNode nullFlavour2 = rawComposition.get("content").get(0).get("items").get(1).get("data").get("events")
                .get(0).get("data").get("items").get(0).get("null_flavour");
        assertThat(nullFlavour2).isNotNull();
        assertThat(nullFlavour2.get("defining_code").get("code_string").asText()).isEqualTo("272");
        assertThat(nullFlavour2.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(nullFlavour2.get("value").asText()).isEqualTo("masked");
    }

    @Test
    public void nullFlavourMapBuild() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NullFlavor4.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();

        JsonNode nullFlavour = rawComposition.get("content").get(0).get("items").get(1).get("data").get("events")
                .get(0).get("data").get("items").get(1).get("null_flavour");
        assertThat(nullFlavour).isNotNull();
        assertThat(nullFlavour.get("defining_code").get("code_string").asText()).isEqualTo("271");
        assertThat(nullFlavour.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(nullFlavour.get("value").asText()).isEqualTo("no information");
        assertThat(rawComposition.get("content").get(0).get("items").get(1).get("data").get("events")
                           .get(0).get("data").get("items").get(1).get("value")).isInstanceOf(NullNode.class);

    }

    @Test
    public void nullFlavourMissingTerminology() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/NullFlavor5.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"),
                objectMapper);
        assertThat(rawComposition).isNotNull();

        JsonNode nullFlavour = rawComposition.get("content").get(0).get("items").get(1).get("data").get("events")
                .get(0).get("data").get("items").get(1).get("null_flavour");
        assertThat(nullFlavour).isNotNull();
        assertThat(nullFlavour.get("defining_code").get("code_string").asText()).isEqualTo("271");
        assertThat(nullFlavour.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(nullFlavour.get("value").asText()).isEqualTo("no information");
    }

    @Test
    public void testNullFlavourDirectValueToJson() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/DemoVitalsComposition.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(structuredComposition).isNotNull();

        JsonNode dvTextNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0)
                .path("any_event").path(0).path("description_of_thermal_stress");
        assertThat(dvTextNode.isArray()).isTrue();
        assertThat(dvTextNode.size()).isEqualTo(1);
        assertThat(dvTextNode.path(0).isObject()).isTrue();
        JsonNode nf = dvTextNode.path(0).path("_null_flavour");
        assertThat(nf.isArray()).isTrue();
        assertThat(nf.size()).isEqualTo(1);
        assertThat(nf.path(0).path("|code").asText()).isEqualTo("271");
        assertThat(nf.path(0).path("|value").asText()).isEqualTo("no information");
        assertThat(nf.path(0).path("|terminology").asText()).isEqualTo("openehr");

    }

    @Test
    public void testNullFlavourDirectValueFromJson() throws Exception {
        String template = getFileContent("/res/Demo Vitals.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/DemoVitalsComposition.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(structuredComposition).isNotNull();

        JsonNode dvTextNode = structuredComposition.path("vitals").path("vitals").path(0).path("body_temperature").path(0).path("any_event").path(0)
                .path("description_of_thermal_stress");
        assertThat(dvTextNode.isArray()).isTrue();
        assertThat(dvTextNode.size()).isEqualTo(1);
        assertThat(dvTextNode.path(0).isObject()).isTrue();
        JsonNode nf = dvTextNode.path(0).path("_null_flavour");
        assertThat(nf.isArray()).isTrue();
        assertThat(nf.size()).isEqualTo(1);
        assertThat(nf.path(0).path("|code").asText()).isEqualTo("271");
        assertThat(nf.path(0).path("|value").asText()).isEqualTo("no information");
        assertThat(nf.path(0).path("|terminology").asText()).isEqualTo("openehr");

        String structuredCompositionString = objectMapper.writeValueAsString(structuredComposition);
        JsonNode retrievedStructuredComposition = objectMapper.readTree(structuredCompositionString);

        JsonNode retrievedRawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                retrievedStructuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test_composer"),
                objectMapper);

        JsonNode nullFlavour = retrievedRawComposition.get("content").get(0).get("items").get(1).get("data").get("events").get(0).get("state").get("items")
                .get(0).get("null_flavour");

        assertThat(nullFlavour).isNotNull();
        assertThat(nullFlavour.get("defining_code").get("code_string").asText()).isEqualTo("271");
        assertThat(nullFlavour.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("openehr");
        assertThat(nullFlavour.get("value").asText()).isEqualTo("no information");

    }

    @Test
    public void simpleTest1() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/composer_name", "Composer")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|code", "253")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|code", "253"));
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|value", "unknown"));
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|terminology", "openehr"));
    }

    @Test
    public void simpleTest2() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/composer_name", "Composer")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|value", "no information")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|code", "271"));
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|value", "no information"));
        assertThat(retrieve).contains(entry("vitals/vitals/body_temperature:0/any_event:0/temperature/_null_flavour|terminology", "openehr"));
    }

    @Test
    public void nullFlavourBroken() throws Exception {
        String template = getFileContent("/res/clinical-summary-events.opt");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/NullFlavor6.json"));
        assertThat(getCompositionValidator().validate(template, rawComposition.toString()));
    }
}
