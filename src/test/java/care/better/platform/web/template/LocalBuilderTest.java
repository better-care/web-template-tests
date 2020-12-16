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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class LocalBuilderTest extends AbstractWebTemplateTest {

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
    public void testName() throws Exception {
        String template = getFileContent("/res/Testing Template N.opt");

        Map<String, Object> flatComposition = ImmutableMap.of(
                "test_encounter/testing/testing/count1", 12,
                "test_encounter/testing/testing/count1/_name|code", "at0001",
                "test_encounter/testing/testing/count1/_name|value", "Hello world");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "CA",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Joe"),
                objectMapper);

        JsonNode nameNode = rawComposition.get("content").get(0).get("data").get("events").get(0).get("data").get("items").get(0).get("items").get(0).get("name");
        assertThat(nameNode.get("@class").asText()).isEqualTo("DV_CODED_TEXT");
        assertThat(nameNode.get("defining_code").get("code_string").asText()).isEqualTo("at0001");
        assertThat(nameNode.get("value").asText()).isEqualTo("Hello world");
    }

    @Test
    public void testBoolean() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("encounter/testing/boolean", true)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);


        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("encounter/testing:0/boolean", true));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);
        assertThat(structuredComposition).isNotNull();
    }

    @Test
    public void testBooleanJson() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = '{' +
                "    \"encounter\": {" +
                "        \"testing\": [" +
                "            {" +
                "                \"boolean\": [" +
                "                    true" +
                "                ]" +
                "            }" +
                "        ]" +
                "    }" +
                '}';
        JsonNode node = mapper.readValue(json, JsonNode.class);

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                node.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("encounter/testing:0/boolean", true));
        JsonNode retrievedJson = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        JsonNode booleanNode = retrievedJson.path("encounter").path("testing").path(0).path("boolean").path(0);
        assertThat(booleanNode.isMissingNode()).isFalse();
        assertThat(booleanNode.isBoolean()).isTrue();
    }

    @Test
    public void testDuration1() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("encounter/testing/duration|xyz", 10)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void testDuration2() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("encounter/testing/duration|year", true)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void testIdentifier() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("encounter/testing/identifier", "id")
                        .put("encounter/testing/identifier|issuer", "issuer")
                        .put("encounter/testing/identifier|assigner", "assigner")
                        .put("encounter/testing/identifier|type", "type")
                        .put("encounter/testing/text", "hi there")
                        .put("encounter/testing/quantity|magnitude", "17,1")
                        .put("encounter/testing/count", "1")
                        .put("encounter/testing/datetime", "2013-1-1T01:00:17.000Z")
                        .put("encounter/testing/duration|year", "1")
                        .put("encounter/testing/ordinal|at0030", "on")
                        .put("encounter/testing/boolean", "true")
                        .put("encounter/testing/proportion", "37,0")
                        .put("encounter/testing/parsable", "<html><body>hello world!</body></html>")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(
                entry("encounter/testing:0/identifier", "id"),
                entry("encounter/testing:0/identifier|issuer", "issuer"),
                entry("encounter/testing:0/identifier|assigner", "assigner"),
                entry("encounter/testing:0/identifier|type", "type"),
                entry("encounter/testing:0/text", "hi there"),
                entry("encounter/testing:0/quantity|magnitude", 17.1),
                entry("encounter/testing:0/quantity|unit", "mm[Hg]"),
                entry("encounter/testing:0/count", 1),
                entry("encounter/testing:0/datetime", "2013-01-01T01:00:17Z"),
                entry("encounter/testing:0/ordinal|code", "at0030"),
                entry("encounter/testing:0/ordinal|ordinal", 2),
                entry("encounter/testing:0/boolean", true),
                entry("encounter/testing:0/proportion", 0.37),
                entry("encounter/testing:0/parsable", "<html><body>hello world!</body></html>")
        );
    }

    @Test
    public void testEmptyIdentifier() throws Exception {
        String template = getFileContent("/res/Testing.opt");
        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("encounter/testing/identifier", "")
                        .put("encounter/testing/identifier|issuer", "issuer")
                        .put("encounter/testing/identifier|assigner", "assigner")
                        .put("encounter/testing/identifier|type", "type")
                        .put("encounter/testing/text", "hi there")
                        .put("encounter/testing/quantity|magnitude", "17,1")
                        .put("encounter/testing/count", "1")
                        .put("encounter/testing/datetime", "2013-1-1T01:00:17.000Z")
                        .put("encounter/testing/duration|year", "1")
                        .put("encounter/testing/ordinal|at0030", "on")
                        .put("encounter/testing/boolean", "true")
                        .put("encounter/testing/proportion", "37,0")
                        .put("encounter/testing/parsable", "<html><body>hello world!</body></html>")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(
                entry("encounter/testing:0/text", "hi there"),
                entry("encounter/testing:0/quantity|magnitude", 17.1),
                entry("encounter/testing:0/quantity|unit", "mm[Hg]"),
                entry("encounter/testing:0/count", 1),
                entry("encounter/testing:0/datetime", "2013-01-01T01:00:17Z"),
                entry("encounter/testing:0/ordinal|code", "at0030"),
                entry("encounter/testing:0/ordinal|ordinal", 2),
                entry("encounter/testing:0/boolean", true),
                entry("encounter/testing:0/proportion", 0.37),
                entry("encounter/testing:0/parsable", "<html><body>hello world!</body></html>"));

        assertThat(retrieved).doesNotContain(
                entry("encounter/testing:0/identifier", ""),
                entry("encounter/testing:0/identifier|issuer", "issuer"),
                entry("encounter/testing:0/identifier|assigner", "assigner"),
                entry("encounter/testing:0/identifier|type", "type"));
    }

    @Test
    public void testNoCtx() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(ImmutableMap.of("encounter/testing/text", "hi there")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(formatted),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition1).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition1.toString())).isEmpty();
    }

    @Test
    public void testTiming() throws Exception {
        String template = getFileContent("/res/Medications.xml");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "en")
                        .put("ctx/territory", "US")
                        .put("ctx/composer_name", "Silvia Blake")
                        .put("ctx/time", "2016-04-21T16:32:39.271+02:00")
                        .put("ctx/id_namespace", "HOSPITAL-NS")
                        .put("ctx/id_scheme", "HOSPITAL-NS")
                        .put("ctx/participation_name", "Dr. Marcus Johnson")
                        .put("ctx/participation_function", "requester")
                        .put("ctx/participation_mode", "face-to-face communication")
                        .put("ctx/participation_id", "199")
                        .put("ctx/participation_name:1", "Lara Markham")
                        .put("ctx/participation_function:1", "performer")
                        .put("ctx/participation_id:1", "198")
                        .put("ctx/health_care_facility|name", "Hospital")
                        .put("ctx/health_care_facility|id", "9091")
                        .put("medications/medication_instruction:0/order:0/medicine", "Medicine 19")
                        .put("medications/medication_instruction:0/order:0/directions", "Directions 96")
                        .put("medications/medication_instruction:0/order:0/timing", "R1")
                        .put("medications/medication_instruction:0/narrative", "Human readable instruction narrative")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void testCodedText() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_status|terminology", "local"),
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_status|code", "at0037"),
                entry("vitals/vitals/haemoglobin_a1c:0/any_event:0/test_status|value", "Začasen"));
    }

    @Test
    public void testMultipleEvents() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "en")
                        .put("ctx/territory", "IE")
                        .put("ctx/composer_name", "John Blake")
                        .put("vitals/vitals/body_temperature/any_event/time", "2014-01-17T22:10:13.000+01:00")
                        .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", "37.1")
                        .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                        .put("vitals/vitals/body_temperature/any_event:1/time", "2014-01-18T07:41:07.000+01:00")
                        .put("vitals/vitals/body_temperature/any_event:1/temperature|magnitude", "38.1")
                        .put("vitals/vitals/body_temperature/any_event:1/temperature|unit", "°C").build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString()));

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(
                entry("vitals/vitals/body_temperature:0/any_event:0/time", "2014-01-17T22:10:13+01:00"),
                entry("vitals/vitals/body_temperature:0/any_event:1/time", "2014-01-18T07:41:07+01:00"),
                entry("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 37.1),
                entry("vitals/vitals/body_temperature:0/any_event:1/temperature|magnitude", 38.1));
    }

    @Test
    public void dependentValues() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "en")
                        .put("ctx/territory", "IE")
                        .put("ctx/composer_name", "John Blake")
                        .put("vitals/vitals/body_temperature/any_event/time", "")
                        .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", "")
                        .put("vitals/vitals/body_temperature/any_event/temperature", "")
                        .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                        .put("vitals/vitals/body_temperature/any_event:1/time", "2014-01-18T07:41:07.000+01:00")
                        .put("vitals/vitals/body_temperature/any_event:1/temperature|magnitude", "38.1")
                        .put("vitals/vitals/body_temperature/any_event:1/temperature|unit", "°C").build();

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

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        assertThat(retrieved).contains(
                entry("vitals/vitals/body_temperature:0/any_event:0/time", "2014-01-18T07:41:07+01:00"),
                entry("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", 38.1));
        assertThat(retrieved.containsKey("vitals/vitals/body_temperature:0/any_event:1/time")).isFalse();
    }

    @Test
    public void testInterval() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("encounter/testing/intervalquantity/lower|magnitude", "101,0")
                        .put("encounter/testing/intervalquantity/lower|unit", "mm[Hg]")
                        .put("encounter/testing/intervalquantity/upper|magnitude", "107,0")
                        .put("encounter/testing/intervalquantity/upper|unit", "mm[Hg]")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(
                entry("encounter/testing:0/intervalquantity/lower|magnitude", 101.0),
                entry("encounter/testing:0/intervalquantity/lower|unit", "mm[Hg]"),
                entry("encounter/testing:0/intervalquantity/upper|magnitude", 107.0),
                entry("encounter/testing:0/intervalquantity/upper|unit", "mm[Hg]"));
    }

    @Test
    public void testLinks() throws Exception {
        String template = getFileContent("/res/Testing.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("encounter/testing/intervalquantity/lower|magnitude", "101,0")
                        .put("encounter/testing/intervalquantity/lower|unit", "mm[Hg]")
                        .put("encounter/testing/intervalquantity/upper|magnitude", "107,0")
                        .put("encounter/testing/intervalquantity/upper|unit", "mm[Hg]")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        JsonNode link = objectMapper.readTree(
                "{\"@class\":\"LINK\",\"meaning\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"meaning\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"type\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"type\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"target\":" +
                        "{\"@class\":\"DV_EHR_URI\",\"value\":\"ehr://abc/def/\"}}");
        ((ArrayNode)rawComposition.get("links")).add(link);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(
                getCompositionConverter().convertFlatToRaw(
                        template,
                        "en",
                        objectMapper.writeValueAsString(retrieved),
                        ImmutableMap.of(
                                CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                        objectMapper)).isNotNull();
    }

    @Test
    public void testDiabetes() throws Exception {
        String template = getFileContent("/res/Diabetes Encounter ver2.xml");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("diabetes_encounter_ver2/problem_diagnosis/problem_diagnosis", "test")
                        .put("diabetes_encounter_ver2/haemoglobin_a1c/any_event/hba1c", "20")
                        .put("diabetes_encounter_ver2/blood_glucose/any_event/glucose_challenge/dose", "10")
                        .put("diabetes_encounter_ver2/blood_glucose/any_event/glucose_challenge/route", "at0.105")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void testDiagnosis() throws Exception {
        String template = getFileContent("/res/Diagnosis.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("diagnosis/diagnosis/diagnosis|code", "A01")
                        .put("diagnosis/diagnosis/diagnosis|value", "test")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("diagnosis/diagnosis:0/diagnosis|terminology", "ICD10"));
    }

    @Test
    public void codedTextWithOther() throws Exception {
        String template = getFileContent("/res/Forms Demo.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("forms_demo/vitals/body_temperature/any_event/body_exposure", "at0033")
                        .put("forms_demo/vitals/body_temperature/any_event/description_of_thermal_stress", "stress 1")
                        .put("forms_demo/vitals/body_temperature/any_event/symptoms|other", "other symptom")
                        .put("forms_demo/vitals/body_temperature/any_event/temperature", "38")
                        .put("forms_demo/vitals/body_temperature/any_event/temperature|unit", "°C")
                        .put("forms_demo/vitals/body_temperature/site_of_measurement", "at0.60")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("forms_demo/vitals/body_temperature:0/any_event:0/symptoms|other", "other symptom"));

        Map<String, String> flatComposition1 = ImmutableMap.<String, String>builder()
                .put("forms_demo/vitals/body_temperature/any_event/description_of_thermal_stress", "s1")
                .put("forms_demo/vitals/body_temperature/any_event/temperature", "38")
                .put("forms_demo/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("forms_demo/vitals/body_temperature/any_event/symptoms", "at0.64")
                .put("forms_demo/vitals/body_temperature/any_event/body_exposure", "at0032")
                .put("forms_demo/vitals/body_temperature/any_event:1/description_of_thermal_stress", "s2")
                .put("forms_demo/vitals/body_temperature/any_event:1/temperature", "39")
                .put("forms_demo/vitals/body_temperature/any_event:1/temperature|unit", "°C")
                .put("forms_demo/vitals/body_temperature/any_event:1/symptoms|other", "xxx")
                .put("forms_demo/vitals/body_temperature/any_event:1/body_exposure", "at0033")
                .put("forms_demo/vitals/body_temperature/site_of_measurement", "at0.60")
                .build();

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition1),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition1).isNotNull();

        Map<String, Object> retrieved1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition1.toString(),
                objectMapper);

        assertThat(retrieved1).contains(
                entry("forms_demo/vitals/body_temperature:0/any_event:0/symptoms|code", "at0.64"),
                entry("forms_demo/vitals/body_temperature:0/any_event:1/symptoms|other", "xxx"));
    }

    @Test
    public void occurencesBug() throws Exception {
        String template = getFileContent("/res/TMDS - Breast pre-operative conference report.opt");

        Map<String, Object> flatComposition =
                new ImmutableMap.Builder<String, Object>()
                        .put("pre-op_conference_report/context/report_name", "at0.0.22")
                        .put("pre-op_conference_report/context/episode_code", "15-08062009")
                        .put("pre-op_conference_report/context/round_number", "1")
                        .put("pre-op_conference_report/context/start_time", new DateTime())
                        .put("pre-op_conference_report/context/report_id", "ASM_691")
                        .put("pre-op_conference_report/context/breast_location:0/specific_location/lesion_unique_number", "1")
                        .put("pre-op_conference_report/context/breast_location:0/specific_location/side", "at0004")
                        .put("pre-op_conference_report/context/breast_location:0/specific_location/breast_compass_position", "2")
                        .put("pre-op_conference_report/context/breast_location:1/specific_location/lesion_unique_number", "2")
                        .put("pre-op_conference_report/context/breast_location:1/specific_location/side", "at0003")
                        .put("pre-op_conference_report/context/breast_location:1/specific_location/breast_compass_position", "3")
                        .put("pre-op_conference_report/breast_pre-op_conclusion_tmds/pre_operative_conclusion", "at0005")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void ordinalLanguage() throws Exception {
        String template = getFileContent("/res/TM - Simple Vital Functions.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("simple_vital_functions/story_or_history/pain/observed_current_intensity/degree", "at0169")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("simple_vital_functions/story_or_history/pain/observed_current_intensity/degree|value", "Nepomemben"));
    }

    @Test
    public void missingUnits() throws Exception {
        String template = getFileContent("/res/inference_engine_result_set3.opt");

        Map<String, Object> flatComposition =
                new ImmutableMap.Builder<String, Object>()
                        .put("inference_engine_result_set_composition/inference_engine_result_set_observation/inference_engine_result_set/result/likelihood|unit", "%")
                        .put("inference_engine_result_set_composition/inference_engine_result_set_observation/inference_engine_result_set/result/likelihood", "0.1")
                        .put("inference_engine_result_set_composition/inference_engine_result_set_observation/inference_engine_result_set/result/disease_code", "R81")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    /*The JSON observation properties are in the order that is different than the order they appear in Template/This
     test checks that even when we specify it in this reversed order, the composition constructed is still in the same order these
    properties are specified in template, web template. */
    @Test
    public void testWebTemplateCompositionOrder() throws Exception {
        String template = getFileContent("/res/Demo Vitals2.opt");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/DemoVitalsStructuredComposition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition.get("content")).hasSize(1);
        assertThat(rawComposition.get("content").get(0).get("@class").asText()).isEqualTo("SECTION");
        JsonNode section = rawComposition.get("content").get(0);
        assertThat(section.get("items")).hasSize(2);
        assertThat(section.get("items").get(0).get("@class").asText()).isEqualTo("OBSERVATION");
        assertThat(section.get("items").get(1).get("@class").asText()).isEqualTo("OBSERVATION");

        JsonNode o1 = section.get("items").get(0);
        JsonNode o2 = section.get("items").get(1);

        assertThat(o1.get("archetype_node_id").asText()).contains("lab_test-hba1c");
        assertThat(o2.get("archetype_node_id").asText()).contains("body_temperature");
    }

    @Test
    public void retrievedXoredDataflatComposition() throws Exception {
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/xoredComposition.json"));
        String template = getFileContent("/res/TMC - Clinical Notes Report.opt");

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve).contains(entry("clinical_notes_report/clinical_notes/clinical_synopses/synopsis/text_value", "matija je tukaj1"));

        retrieve.remove("clinical_notes_report/clinical_notes/clinical_synopses/synopsis/text_value");
        retrieve.put("clinical_notes_report/clinical_notes/clinical_synopses/synopsis/value", "html text");
        retrieve.put("clinical_notes_report/clinical_notes/clinical_synopses/synopsis/value|formalism", "text/plain");

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(retrieve),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieve2 = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition2.toString(),
                objectMapper);

        assertThat(retrieve2).contains(entry("clinical_notes_report/clinical_notes/clinical_synopses/synopsis/parsable_value", "html text"));
    }

    @Test
    public void runtimeNameConstraints() throws Exception {
        String template = getFileContent("/res/TMC - ICU -Ventilator device Report.opt");

        Map<String, Object> flatComposition =
                new ImmutableMap.Builder<String, Object>()
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/epap|magnitude", 101.0)
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/epap|unit", "mbar")
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/ipap|magnitude", 102.0)
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/ipap|unit", "mbar")
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/peep|magnitude", 103.0)
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/peep|unit", "mbar")
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/map_-_mean_airway_pressure_central_pressure|magnitude", 104.0)
                        .put("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/map_-_mean_airway_pressure_central_pressure|unit", "mbar")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        JsonNode itemsNode = rawComposition.get("content").get(0).get("items").get(0).get("data").get("events").get(0).get("data").get("items").get(0).get(
                "items");
        assertThat(itemsNode.get(0).get("name").get("value").asText()).isEqualTo("PEEP");
        assertThat(itemsNode.get(1).get("name").get("value").asText()).isEqualTo("IPAP");
        assertThat(itemsNode.get(2).get("name").get("value").asText()).isEqualTo("EPAP");
        assertThat(itemsNode.get(3).get("name").get("value").asText()).isEqualTo("MAP - Mean airway pressure / central pressure");

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve).contains(
                entry("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/epap:0|magnitude", 101.0),
                entry("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/ipap:0|magnitude", 102.0),
                entry("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/peep:0|magnitude", 103.0),
                entry("ventilator_device_report/nbp840/nbp840_observtions/ventilator_findings/map_-_mean_airway_pressure_central_pressure:0|magnitude", 104.0));
    }

    @Test
    public void dvTextListOfValuesFixedValue() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, Object> flatComposition =
                new ImmutableMap.Builder<String, Object>()
                        .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                        .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                        .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("vitals/vitals/body_temperature:0/any_event:0/description_of_thermal_stress", "Fixed value"));
    }

    @Test
    public void proportionFieldType() throws Exception {
        String template = getFileContent("/res/Vital Signs.xml");
        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/proportion.json"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(structuredComposition.path("vital_signs").path("indirect_oximetry").path(0).path("spo2").path(0).path("|numerator").floatValue()).isEqualTo(79.21f);
    }

    @Test
    public void durationValidation() throws Exception {
        String template = getFileContent("/res/Patient Diagnosis (composition).xml");

        String jsonString = getFileContent("/res/Local1.json");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                jsonString,
                Collections.emptyMap(),
                objectMapper);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void idNamespace() throws Exception {
        String template = getFileContent("/res/Patient Diagnosis (composition).xml");

        String jsonString = getFileContent("/res/Local2.json");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                jsonString,
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition.get("context").get("health_care_facility").get("external_ref").get("namespace").asText()).isEqualTo("NHSEngland");
    }

    /*
    This test works with a template with a HISTORY with 2 children:
    - point event 0..*
    - interval event 0..1
     */
    @Test
    public void anyEventWithIntervalEvent() throws Exception {
        String template = getFileContent("/res/Test Template.xml");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(new ImmutableMap.Builder<String, Object>()
                                                        .put("test_composition/blood_pressure/any_event/systolic|magnitude", 120.0)
                                                        .put("test_composition/blood_pressure/any_event/systolic|unit", "mm[Hg]")
                                                        .build()),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(new ImmutableMap.Builder<String, Object>()
                                                        .put("test_composition/blood_pressure/a24_hour_average/systolic|magnitude", 120.0)
                                                        .put("test_composition/blood_pressure/a24_hour_average/systolic|unit", "mm[Hg]")
                                                        .build()),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(rawComposition2).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition2.toString())).isEmpty();
    }

    @Test
    public void partialDate() throws Exception {
        String template = getFileContent("/res/Testing Template N5.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-01")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved.get("test_encounter/testing:0/testing:0/partial_date")).isEqualTo("2016-01");

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-01-01")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieved2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(retrieved2.get("test_encounter/testing:0/testing:0/partial_date")).isEqualTo(LocalDate.of(2016, 1, 1).toString());
    }

    @Test
    public void invalidPartialDate1() throws Exception {
        String template = getFileContent("/res/Testing Template N5.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-13")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void invalidPartialDate2() throws Exception {
        String template = getFileContent("/res/Testing Template N5.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "z2016-12")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void partialDateXX() throws Exception {
        String template = getFileContent("/res/Testing Template N6.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-01")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper);

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved.get("test_encounter/testing:0/testing:0/partial_date")).isEqualTo("2016-01");

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-12-01")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper);

        Map<String, Object> retrieved2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);
        assertThat(retrieved2.get("test_encounter/testing:0/testing:0/partial_date")).isEqualTo("2016-12");
    }

    @Test
    public void invalidPartialDateXX1() throws Exception {
        String template = getFileContent("/res/Testing Template N6.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("test_encounter/testing:0/testing:0/partial_date", "2016-13")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void missingValue() throws Exception {
        String template = getFileContent("/res/clinical-summary-events2.opt");

        String rawCompositionString = getFileContent("/res/localMissingValue.json");

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawCompositionString,
                objectMapper);

        assertThat(retrieve.get("clinical_summary_events/laboratory_exams/laboratory_exams_results/pathology_test_result:1/any_event:1/result_group/result/result_value/quantity_value|magnitude"))
                .isNotNull();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(retrieve),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "test"),
                objectMapper);

        JsonNode itemsNode = rawComposition.get("content").get(7).get("items").get(1).get("items").get(1).get("data").get("events").get(1).get("data").get("items").get(5).get("items").get(0).get("items");
        assertThat(itemsNode.get(0).get("@class").asText()).isEqualTo("ELEMENT");
        assertThat(itemsNode.get(0).get("name").get("value").asText()).isEqualTo("Result Value");
        assertThat(itemsNode.get(0).get("value").get("@class").asText()).isEqualTo("DV_QUANTITY");
        assertThat(itemsNode.get(0).get("value").get("magnitude").asDouble()).isEqualTo(7.6);
    }

    @Test
    public void intervalEventWidth() throws Exception {
        String template = getFileContent("/res/Liver Donor.xml");

        String jsonString = getFileContent("/res/Local3.json");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                jsonString,
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void structuredNoPipes() throws Exception {
        String template = getFileContent("/res/Liver Recipient Information.xml");

        String jsonString = getFileContent("/res/Local4.json");

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                jsonString,
                Collections.emptyMap(),
                objectMapper);
        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void termMappings() throws Exception {
        String template = getFileContent("/res/Demo Vitals term mapping.opt");

        Map<String, Object> flatComposition =
                ImmutableMap.<String, Object>builder()
                        .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                        .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                        .put("vitals/vitals/body_temperature/any_event/symptoms", "at0.64")
                        .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.TERM_BINDING_TERMINOLOGIES.getKey(), "*"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
        JsonNode valueNode = rawComposition.get("content").get(0).get("items").get(0).get("data").get("events").get(0).get("data").get("items").get(1).get("value");

        assertThat(valueNode.get("@class").asText()).isEqualTo("DV_CODED_TEXT");
        assertThat(valueNode.get("mappings").get(0).get("match").asText()).isEqualTo("=");
        assertThat(valueNode.get("mappings").get(0).get("target").get("terminology_id").get("value").asText()).isEqualTo("LNC205");
        assertThat(valueNode.get("mappings").get(0).get("target").get("code_string").asText()).isEqualTo("1111");
    }

    @Test
    public void serverError() throws Exception {
        String template = getFileContent("/res/Adverse Reaction List.v1.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("ctx/language", "en")
                .put("ctx/territory", "GB")
                .put("adverse_reaction_list/composer|name", "Dominic Slatford")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/causative_agent", "Nuts")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/status|value", "E.40")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/status/defining_code", "Likely")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/reaction_details/manifestation:0", "Somthing might happen")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/reaction_details/record_provenance/information_source", "Patient")
                .put("adverse_reaction_list/allergies_and_adverse_reactions/adverse_reaction_risk:0/last_updated", "2018-02-13T11:52:41.8090137+00:00")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void cytologyIssue() throws Exception {
        String template = getFileContent("/res/Cytology Report.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/cytology.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition).isNotNull();
    }

    @Test
    public void historyFixedOffset() throws Exception {
        String template = getFileContent("/res/Apgar_1.opt");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/apgar_composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void historyFixedOffsetInvalidContent() throws Exception {
        String template = getFileContent("/res/Apgar_1.opt");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/apgar_composition_with_invalidtimes.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).hasSize(1);
    }
}
