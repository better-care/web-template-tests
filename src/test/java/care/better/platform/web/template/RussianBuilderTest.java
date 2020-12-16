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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class RussianBuilderTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;
    private Map context;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        context = ImmutableMap.of(
                CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer");
    }

    @Test
    public void externalTerminology() throws Exception {
        String template = getFileContent("/res/NSI_test_information.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("информация/справочная_информация/из_справочника", "123")
                .put("информация/справочная_информация/из_справочника|value", "Первая категория")
                .put("информация/справочная_информация/time", "2012-12-07T02:46:03")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve.get("информация/справочная_информация:0/из_справочника|code")).isEqualTo("123");
        assertThat(retrieve.get("информация/справочная_информация:0/из_справочника|value")).isEqualTo("Первая категория");
    }

    @Test
    public void testInvalidOccurences1() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_gynecologist_anamnesis_pregnant .v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian5.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve.get("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:0/непереносимость_лекарственных_средств")).isEqualTo("л");
        assertThat(retrieve.get("прием_пациента/перенесенные_заболевания_и_операции:0/аллергологический_анамнез:1/непереносимость_лекарственных_средств")).isEqualTo("г");
    }

    @Test
    public void testInvalidOccurences2() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_gynecologist_prophylactic_examination.v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian6.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve.get("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:0/локализация|code")).isEqualTo("at0010");
        assertThat(retrieve.get("прием_пациента/общий_осмотр/периферические_лимфатические_узлы:0/лимфатические_узлы_увеличены:1/локализация|code")).isEqualTo("at0009");
    }

    @Test
    public void testActivity() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_new_physiatrist_examination.v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian1.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();
    }

    @Test
    public void testActivityWithProviders() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_new_physiatrist_examination.v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian2.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.ACTION_TO_INSTRUCTION_HANDLER.getKey(), "Description of what instruction is about!",
                        CompositionBuilderContextKey.ACTIVITY_TIMING_PROVIDER.getKey(), "timing::R0"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void testDentalFormuleValidationSuccess() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_dental_formule.v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian3.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();
    }

    @Test
    public void testLinks() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_dermatologist_examination.v1.xml");

        JsonNode rawComposition = objectMapper.readTree(getFileContent("/res/composition_with_Dv_Interval.json"));
        JsonNode link1 = objectMapper.readTree(
                "{\"@class\":\"LINK\",\"meaning\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"follow up\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"type\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"issue\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"target\":" +
                        "{\"@class\":\"DV_EHR_URI\",\"value\":\"ehr:///c1\"}}");

        JsonNode link2 = objectMapper.readTree(
                "{\"@class\":\"LINK\",\"meaning\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"follow up2\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"type\":" +
                        "{\"@class\":\"DV_TEXT\",\"value\":\"issue2\",\"hyperlink\":null,\"formatting\":null,\"mappings\":[],\"language\":null,\"encoding\":null},\"target\":" +
                        "{\"@class\":\"DV_EHR_URI\",\"value\":\"ehr:///c2\"}}");

        ((ArrayNode)rawComposition.get("links")).add(link1);
        ((ArrayNode)rawComposition.get("links")).add(link2);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve).contains(
                entry("прием_пациента_врачом-дерматолог/_link:0|meaning", "follow up"),
                entry("прием_пациента_врачом-дерматолог/_link:0|type", "issue"),
                entry("прием_пациента_врачом-дерматолог/_link:0|target", "ehr:///c1"),
                entry("прием_пациента_врачом-дерматолог/_link:1|meaning", "follow up2"),
                entry("прием_пациента_врачом-дерматолог/_link:1|type", "issue2"),
                entry("прием_пациента_врачом-дерматолог/_link:1|target", "ehr:///c2")
        );
    }

    @Test
    public void testQuantityValidationAndConversion() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_interval_quantity_test.v1.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/Russian4.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);
        assertThat(retrieve).contains(entry("test/interval_quantity/fiels_for_test/lower|magnitude", 120.0));
    }

    @Test
    public void missingInstruction() throws Exception {
        String template = getFileContent("/res/opt referral.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/missingInstruction.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("content").get(0).get("items").get(0).get("@class").asText()).isEqualTo("INSTRUCTION");
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).hasSize(1);
    }

    @Test
    public void testTherapistExaminationContent() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_therapist_examination.opt");

        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/therapist_saved.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);

        assertThat(rawComposition.get("content")).hasSize(5);
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> flatComposition2 = objectMapper.readValue(getFileContent("/res/therapist_saved_fixed.json"), new TypeReference<Map<String, Object>>() {});

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(flatComposition2),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);

        assertThat(rawComposition2.get("content")).hasSize(6);
        assertThat(rawComposition2.get("content").get(5).get("name").get("value").asText()).isEqualTo("Сведения о выполнении назначения");
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition2.toString(), true, true)).isEmpty();
    }

    @Test
    public void testCardioligistExaminationValidation() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_cardiologist_examination.v3.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/cardio.json"));
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);

        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).hasSize(1);

        String template2 = getFileContent("/res/openEHR-EHR-COMPOSITION.t_cardiologist_examination.v3-fix.xml");

        JsonNode rawComposition2 = getCompositionConverter().convertStructuredToRaw(
                template2,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);

        assertThat(getCompositionValidator().validateWithParams(template2, rawComposition2.toString(), true, true)).isEmpty();
    }

    @Test
    public void testPrevaccinalExaminationValidation() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.prevaccinal_examination.opt");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/vaccination.json"));
        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "RU"),
                objectMapper);
        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).hasSize(2);
    }

    @Test
    public void testVaccinationCardYear() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.vaccination_card.opt");
        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/ru-composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "ru"),
                objectMapper);

        assertThat(getCompositionValidator().validateWithParams(template, rawComposition.toString(), true, true)).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "ru",
                rawComposition.toString(),
                objectMapper);
        assertThat(retrieve)
                .contains(entry("карта_профилактических_прививок/туберкулезные_пробы/заготовка_заголовка:0/результат_иммунодиагностики/дата", "2013"));
    }

    @Test
    public void testBodyTemperatureConversion() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/time", "2015-01-01T10:00:00.000+05:00")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|magnitude", "37.7")
                .put("vitals/vitals/body_temperature:0/any_event:0/temperature|unit", "°C")
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

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(retrieved),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.LOCALE.getKey(), new Locale("ru")),
                objectMapper);

        assertThat(rawComposition1).isNotNull();
    }

    @Test
    public void testSociomedicalAssessmentContent() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_sociomedical_assessment_referral.v1.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/ru559_composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                context,
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("content").get(7).get("data").get("items").get(0).get("items").get(0).get("value").get("@class").asText()).isEqualTo("DV_TEXT");
    }

    @Test
    public void testReferenceFormConversion() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_outpatient_reference_form_025_1.v4.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/EMIASSIMI-3836-composition.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "ru",
                structuredComposition.toString(),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.LOCALE.getKey(), new Locale("ru", "RU")),
                objectMapper);

        assertThat(rawComposition).isNotNull();
    }
}
