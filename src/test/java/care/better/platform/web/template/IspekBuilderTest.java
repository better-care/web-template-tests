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
import com.google.common.collect.Maps;
import org.joda.time.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class IspekBuilderTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;
    private ImmutableMap context;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        context = ImmutableMap.of(
                CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer");

    }

    @Test
    public void vitalsSingleNonCompact() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/context/setting|code", "238")
                .put("vitals/context/setting|value", "other care")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", "37.7")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);
        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void vitalsSingle() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/vitals/haemoglobin_a1c/any_event/hba1c", "5,1")
                .put("vitals/vitals/haemoglobin_a1c/datetime_result_issued", "20.1.2012 19:30")
                .put("vitals/vitals/body_temperature/any_event/time", "1.1.2012 0:0")
                .put("vitals/vitals/body_temperature/site_of_measurement", "at0022")
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", "38,1")
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature:1/any_event/temperature|magnitude", "39,1")
                .put("vitals/vitals/body_temperature:1/any_event/temperature|unit", "°C")
                .put("ctx/time", "1.2.2012 00:00")
                .put("ctx/category", "event")
                .put("ctx/setting", "dental care")
                .put("ctx/id_schema", "local_sch")
                .put("ctx/id_namespace", "local_ns")
                .put("ctx/provider_name", "Pippa Smith")
                .put("ctx/provider_id", "197")
                .put("ctx/participation_name", "Edna Smith")
                .put("ctx/participation_function", "performer")
                .put("ctx/participation_mode", "face-to-face communication")
                .put("ctx/participation_id", "199")
                .put("ctx/participation_name:1", "John Smith")
                .put("ctx/participation_function:1", "executor")
                .put("ctx/participation_mode:1", "interpreted audio-only")
                .put("ctx/participation_id:1", "198")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);
        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"Demo Vitals\"},\"rm_version\":\"1.0.4\"}"));
        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("context").get("start_time").get("value").asText()).isEqualTo("2012-02-01T00:00:00+01:00");

        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("name").get("value").asText()).isEqualTo("Vitals");

        JsonNode hba1c = section.get("items").get(0);
        assertThat(hba1c.get("name").get("value").asText()).isEqualTo("Haemoglobin A1c");

        JsonNode history2 = hba1c.get("data");
        assertThat(history2.get("origin").get("value").asText()).isEqualTo("2012-02-01T00:00:00+01:00");

        JsonNode data2 = history2.get("events").get(0).get("data");
        JsonNode element2 = data2.get("items").get(0);
        JsonNode value2 = element2.get("value");

        assertThat(value2.get("numerator").asDouble()).isEqualTo(5.1);
        assertThat(value2.get("denominator").asDouble()).isEqualTo(100.0);
        assertThat(value2.get("type").asLong()).isEqualTo(2L);
        assertThat(value2.get("precision")).isInstanceOf(NullNode.class);

        JsonNode provider = hba1c.get("provider");
        assertThat(provider.get("external_ref").get("namespace").asText()).isEqualTo("local_ns");
        assertThat(provider.get("external_ref").get("id").get("value").asText()).isEqualTo("197");
        assertThat(provider.get("name").asText()).isEqualTo("Pippa Smith");

        JsonNode participation0 = hba1c.get("other_participations").get(0);
        JsonNode participation1 = hba1c.get("other_participations").get(1);
        assertThat(participation0.get("mode").get("defining_code").get("code_string").asText()).isEqualTo("216");
        assertThat(participation0.get("function").get("value").asText()).isEqualTo("performer");
        assertThat(participation0.get("performer").get("name").asText()).isEqualTo("Edna Smith");
        assertThat(participation0.get("performer").get("external_ref").get("id").get("value").asText()).isEqualTo("199");
        assertThat(participation1.get("mode").get("defining_code").get("code_string").asText()).isEqualTo("222");
        assertThat(participation1.get("function").get("value").asText()).isEqualTo("executor");
        assertThat(participation1.get("performer").get("name").asText()).isEqualTo("John Smith");
        assertThat(participation1.get("performer").get("external_ref").get("id").get("value").asText()).isEqualTo("198");

        JsonNode temp0 = section.get("items").get(1);
        assertThat(temp0.get("name").get("value").asText()).isEqualTo("Body temperature");

        JsonNode protocol0 = temp0.get("protocol");
        JsonNode element0 = protocol0.get("items").get(0);
        JsonNode value0 = element0.get("value");
        assertThat(value0.get("defining_code").get("code_string").asText()).isEqualTo("at0022");
        assertThat(value0.get("defining_code").get("terminology_id").get("value").asText()).isEqualTo("local");
        assertThat(value0.get("value").asText()).isEqualTo("Usta");

        assertThat(section.get("items").get(2).get("name").get("value").asText()).isEqualTo("Body temperature #2");
        JsonNode tree = temp0.get("data").get("events").get(0).get("data");

        JsonNode tempElement = tree.get("items").get(0);
        JsonNode tempElementValue = tempElement.get("value");
        assertThat(tempElementValue.get("precision").asInt()).isEqualTo(1);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void vitalsSingle1() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/vitals/haemoglobin_a1c/datetime_result_issued", "1/2/2012 8:07")
                .put("vitals/vitals/haemoglobin_a1c/receiver_order_identifier", "rec")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_status", "at0038")
                .put("vitals/vitals/haemoglobin_a1c/any_event/hba1c", "3.2")
                .put("vitals/vitals/haemoglobin_a1c/any_event/overall_interpretation", "overall interp")
                .put("vitals/vitals/haemoglobin_a1c/any_event/diagnostic_service", "diag")
                .put("vitals/vitals/haemoglobin_a1c/laboratory_test_result_identifier", "lab")
                .put("vitals/vitals/haemoglobin_a1c/any_event/test_name", "test name")
                .put("vitals/vitals/haemoglobin_a1c/requestor_order_identifier", "req")
                .put("ctx/participation_mode:0", "face-to-face communication")
                .put("ctx/territory", "IE")
                .put("ctx/category", "event")
                .put("ctx/action_ism_transition_current_state", "initial")
                .put("ctx/id_schema", "local")
                .put("ctx/action_time", "now")
                .put("ctx/time", "now")
                .put("ctx/setting", "other care")
                .put("ctx/language", "en")
                .put("ctx/id_namespace", "local")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "IE",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"Demo Vitals\"},\"rm_version\":\"1.0.4\"}"));

        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("name").get("value").asText()).isEqualTo("Vitals");

        JsonNode hba1c = section.get("items").get(0);
        assertThat(hba1c.get("name").get("value").asText()).isEqualTo("Haemoglobin A1c");

        JsonNode history = hba1c.get("data");
        assertThat(history.get("events")).hasSize(1);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();


        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/hba1c", 0.032),
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/hba1c|numerator", 3.2),
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/hba1c|denominator", 100.0));

        Map<String, Object> retrieveFormatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);
        assertThat(retrieveFormatted).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/hba1c", 0.032));
    }

    @Test
    public void vitalsSingle2() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", "39")
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature/any_event/symptoms|at0.65", "on")
                .put("vitals/vitals/body_temperature/any_event/symptoms|at0.64", "on")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "IE",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.LOCALE.getKey(), new Locale("en", "IE")),
                objectMapper);

        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"Demo Vitals\"},\"rm_version\":\"1.0.4\"}"));

        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("name").get("value").asText()).isEqualTo("Vitals");

        JsonNode temp = section.get("items").get(0);
        assertThat(temp.get("name").get("value").asText()).isEqualTo("Body temperature");

        JsonNode history = temp.get("data");
        assertThat(history.get("events")).hasSize(1);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieveTyped = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);
        assertThat(retrieveTyped).contains(
                entry("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 39.0),
                entry("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C"),
                entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:0|code", "at0.64"),
                entry("vitals/vitals/body_temperature:0/any_event:0/symptoms:1|code", "at0.65"));
    }

    @Test
    public void perinatal() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Perinatal history Summary.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/time", "1.2.2012 00:01")
                .put("ctx/category", "persistent")
                .put("ctx/history_origin", "1.2.2012 00:01")
                .put("perinatal_history/perinatal_history/apgar_score/a1_minute/total", "3")
                .put("perinatal_history/perinatal_history/apgar_score/a10_minute/total", "5")
                .put("perinatal_history/perinatal_history/maternal_pregnancy/labour_or_delivery/duration_of_labour|day", "1")
                .put("perinatal_history/perinatal_history/maternal_pregnancy/labour_or_delivery/duration_of_labour|hour", "2")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper
        );
        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"ISPEK - MED - Perinatal history Summary\"},\"rm_version\":\"1.0.4\"}"));
        assertThat(rawComposition).isNotNull();

        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("name").get("value").asText()).isEqualTo("Perinatal history");

        JsonNode obs0 = section.get("items").get(1);
        assertThat(obs0.get("name").get("value").asText()).isEqualTo("Apgar score");

        JsonNode history = obs0.get("data");
        assertThat(history.get("origin").get("value").asText()).isEqualTo("2012-02-01T00:01:00+01:00");
        JsonNode event0 = history.get("events").get(0);
        JsonNode event1 = history.get("events").get(1);

        assertThat(LocalDateTime.parse(event0.get("time").get("value").asText().split("\\+")[0]))
                .isEqualTo(LocalDateTime.parse(history.get("origin").get("value").asText().split("\\+")[0]).plusMinutes(1L));
        assertThat(LocalDateTime.parse(event1.get("time").get("value").asText().split("\\+")[0]))
                .isEqualTo(LocalDateTime.parse(history.get("origin").get("value").asText().split("\\+")[0]).plusMinutes(10L));

        JsonNode data0 = event0.get("data");
        JsonNode value0 = data0.get("items").get(0).get("value");
        assertThat(value0.get("magnitude").asLong()).isEqualTo(3L);

        JsonNode data1 = event1.get("data");
        JsonNode value1 = data1.get("items").get(0).get("value");
        assertThat(value1.get("magnitude").asLong()).isEqualTo(5L);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieveTyped = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);
        assertThat(retrieveTyped).contains(
                entry("perinatal_history/perinatal_history/apgar_score:0/a1_minute/total", 3),
                entry("perinatal_history/perinatal_history/apgar_score:0/a10_minute/total", 5),
                entry("perinatal_history/perinatal_history/maternal_pregnancy:0/labour_or_delivery:0/duration_of_labour",
                      new Period(0, 0, 0, 1, 2, 0, 0, 0).toString()));
    }

    @Test
    public void perinatal2() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Perinatal history Summary.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("perinatal_history/perinatal_history/maternal_pregnancy/maternal_age|year", "33")
                .put("perinatal_history/perinatal_history/maternal_pregnancy/significant_family_history/family_issue", "at0461")
                .put("perinatal_history/perinatal_history/maternal_pregnancy/number_of_previous_pregnancies", "2")
                .put("perinatal_history/perinatal_history/maternal_pregnancy/gynaecological_history/gynaecological_issue", "at0469")
                .put("ctx/participation_mode:0", "face-to-face communication")
                .put("ctx/category", "persistent")
                .put("ctx/action_ism_transition_current_state", "initial")
                .put("ctx/id_schema", "local")
                .put("ctx/action_time", "now")
                .put("ctx/time", "now")
                .put("ctx/setting", "other care")
                .put("ctx/territory", "IE")
                .put("ctx/language", "en")
                .put("ctx/id_namespace", "local")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"ISPEK - MED - Perinatal history Summary\"},\"rm_version\":\"1.0.4\"}"));
        assertThat(rawComposition).isNotNull();

        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("name").get("value").asText()).isEqualTo("Perinatal history");

        JsonNode obs0 = section.get("items").get(0);
        assertThat(obs0.get("name").get("value").asText()).isEqualTo("Maternal pregnancy");

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void initialMedicationSafety() throws Exception {
        String template = getFileContent("/res/ISPEK - MSE - Initial Medication Safety Report.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/reaction|code", "ac001")
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/reaction|value", "Value")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        ((ObjectNode)rawComposition).set("archetype_details", objectMapper.readTree("{\"@class\":\"ARCHETYPED\",\"archetype_id\":" +
                                                                                            "{\"@class\":\"ARCHETYPE_ID\",\"value\":\"openEHR-EHR-COMPOSITION.encounter.v1\"},\"template_id\":" +
                                                                                            "{\"@class\":\"TEMPLATE_ID\",\"value\":\"ISPEK - MSE - Initial Medication Safety Report\"},\"rm_version\":\"1.0.4\"}"));
        assertThat(rawComposition).isNotNull();

        JsonNode evaluation = rawComposition.get("content").get(0);
        JsonNode tree = evaluation.get("data");
        JsonNode cluster = tree.get("items").get(0);
        JsonNode element = cluster.get("items").get(0);
        assertThat(element.get("value").get("@class").asText()).isEqualTo("DV_CODED_TEXT");

        JsonNode codedText = element.get("value");
        assertThat(codedText.get("defining_code").get("code_string").asText()).isEqualTo("ac001");
        assertThat(codedText.get("value").asText()).isEqualTo("Value");

        Map<String, Object> map = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);
        assertThat(map).contains(
                entry("initial_medication_safety_report/medication_safety_event:0/adverse_effect:0/reaction|code", "ac001"),
                entry("initial_medication_safety_report/medication_safety_event:0/adverse_effect:0/reaction|value", "Value"));
    }

    @Test
    public void document() throws Exception {
        String template = getFileContent("/res/MED - Document.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("medical_document/document/date_last_reviewed", "2012-12-01T10:17:00.000+01:00")
                .put("medical_document/document/content", "Hello world!")
                .put("medical_document/document/status", "at0007")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);
        assertThat(rawComposition).isNotNull();

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void vitalsFixedflatComposition() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Vital Functions Encounter.opt");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Ispek2.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);
        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).hasSize(3);
    }

    @Test
    public void vitalsJson() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Vital Functions Encounter.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("vital_functions/vital_signs/blood_pressure/any_event/diastolic|magnitude", "90")
                .put("vital_functions/vital_signs/blood_pressure/any_event/diastolic|unit", "mm[Hg]")
                .put("vital_functions/vital_signs/blood_pressure/any_event/systolic|magnitude", "120")
                .put("vital_functions/vital_signs/blood_pressure/any_event/systolic|unit", "mm[Hg]")
                .put("vital_functions/vital_signs/body_mass_index:0/any_event:0/body_mass_index|unit", "kg/m2")
                .put("vital_functions/vital_signs/body_temperature:0/any_event:0/body_temperature|magnitude", "37")
                .put("vital_functions/vital_signs/body_temperature:0/any_event:0/body_temperature|unit", "°C")
                .put("vital_functions/vital_signs/body_temperature:0/any_event:0/symptoms", "at0.65")
                .put("vital_functions/vital_signs/body_temperature:0/location_of_measurement", "at0.60")
                .put("vital_functions/vital_signs/body_weight:0/any_event:0/body_weight|magnitude", "40")
                .put("vital_functions/vital_signs/body_weight:0/any_event:0/body_weight|unit", "kg")
                .put("vital_functions/vital_signs/height_length:0/any_event:0/body_height_length|magnitude", "70")
                .put("vital_functions/vital_signs/height_length:0/any_event:0/body_height_length|unit", "cm")
                .put("vital_functions/vital_signs/pulse/any_event/heart_rate|magnitude", "130")
                .put("vital_functions/vital_signs/pulse/any_event/heart_rate|unit", "/min")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);
        assertThat(rawComposition).isNotNull();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                context,
                objectMapper)).isNotNull();
    }

    @Test
    public void simpleBodyObservation() throws Exception {
        String template = getFileContent("/res/TM - Simple Body Observation2.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Ispek1.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void compositionUpdate() throws Exception {
        String template = getFileContent("/res/TM - Simple Body Observation2.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Ispek3.json"), new TypeReference<Map<String, Object>>() {});
        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> delta = ImmutableMap.<String, Object>builder()
                .put("simple_body_observation/simptomi_bolečine/pain/comments", "pain comments")
                .put("simple_body_observation/simptomi_bolečine/pain/intensity/pain_scale_method|code", "at0.0.202")
                .put("simple_body_observation/simptomi_bolečine/pain/intensity/scale_score", 3)
                .build();

        JsonNode updatedComposition = getCompositionConverter().updateRawComposition(
                template,
                "en",
                rawComposition.toString(),
                context,
                delta,
                objectMapper);

        assertThat(getCompositionValidator().validate(template, updatedComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                updatedComposition.toString(),
                objectMapper);

        Map<String, Object> filtered = Maps.filterEntries(retrieve, input -> input.getKey().startsWith("simple_body_observation/simptomi_bolečine/pain"));

        assertThat(filtered).hasSize(5);

        assertThat(filtered).contains(
                entry("simple_body_observation/simptomi_bolečine/pain/comments", "pain comments"),
                entry("simple_body_observation/simptomi_bolečine/pain/intensity/pain_scale_method|code", "at0.0.202"),
                entry("simple_body_observation/simptomi_bolečine/pain/intensity/pain_scale_method|value", "Lestvica VAS"),
                entry("simple_body_observation/simptomi_bolečine/pain/intensity/pain_scale_method|terminology", "local"),
                entry("simple_body_observation/simptomi_bolečine/pain/intensity/scale_score", 3));
    }

    @Test
    public void compositionUpdateFailed() throws Exception {
        String template = getFileContent("/res/TM - Discharge Plan Encounter.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/CompositionPediatrics.json"));

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> delta = new HashMap<>();
        delta.put("discharge_plan_encounter/discharge_plan/healthcare_service_request/request/discharge_intervention_service_requested|at0.196", true);
        delta.put("discharge_plan_encounter/discharge_plan/healthcare_service_request/request/discharge_intervention_service_requested|at0.197", null);
        delta.put("discharge_plan_encounter/discharge_plan/healthcare_service_request/request/discharge_intervention_service_requested|at0.198", null);
        delta.put("discharge_plan_encounter/discharge_plan/healthcare_service_request/request/discharge_intervention_service_requested|at0.199", null);

        JsonNode updatedComposition = getCompositionConverter().updateRawComposition(
                template,
                "en",
                rawComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.INSTRUCTIONS_NARRATIVE_PROVIDER.getKey(), "narrative",
                        CompositionBuilderContextKey.ACTIVITY_TIMING_PROVIDER.getKey(), "timing::R0"),
                delta,
                objectMapper);
        assertThat(getCompositionValidator().validate(template, updatedComposition.toString())).isEmpty();
    }

    @Test
    public void activityTiming() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Nursing careplan Encounter.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/CompositionCareplan.json"));
        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(structuredComposition).isNotNull();
    }

    @Test
    public void compositionWithInstructionsAndActionsUpdateFailed() throws Exception {
        String template = getFileContent("/res/TM - Discharge Plan Encounter.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/CompositionPediatrics2.json"));

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> delta = new HashMap<>();
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.2", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.3", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.4", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.5", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.6", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.7", "true");
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.8", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.9", null);
        delta.put("discharge_plan_encounter/discharge_plan/goal_setting/discharge_goal|at0.0.10", null);
        delta.put("discharge_plan_encounter/context/context_detail/period_of_care_identifier", "76567450");

        JsonNode updatedComposition = getCompositionConverter().updateRawComposition(
                template,
                "en",
                rawComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.ISM_TRANSITION.getKey(), "openehr::524::initial"),
                delta,
                objectMapper);

        assertThat(getCompositionValidator().validate(template, updatedComposition.toString())).isEmpty();
    }

    @Test
    public void order() throws Exception {
        String template = getFileContent("/res/TM - Discharge Activity Plan Encounter.xml");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/careplan_activities.json"));

        ImmutableMap<String, Object> tempContext = ImmutableMap.<String, Object>builder()
                .put(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl")
                .put(CompositionBuilderContextKey.TERRITORY.getKey(), "SI")
                .put(CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer")
                .put(CompositionBuilderContextKey.ISM_TRANSITION.getKey(), "openehr::524::initial")
                .put(CompositionBuilderContextKey.INSTRUCTIONS_NARRATIVE_PROVIDER.getKey(), "narrative")
                .put(CompositionBuilderContextKey.ACTIVITY_TIMING_PROVIDER.getKey(), "timing::R0").build();

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                tempContext,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        JsonNode retrieved = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved.path("discharge_activity_plan_encounter")
                           .path("discharge_activity_plan").path(0)
                           .path("healthcare_service_request").path(4)
                           .path("request").path(0)
                           .path("discharge_intervention_service_requested").path(0)
                           .path("|code").asText()).isEqualTo("100.05");
    }
}
