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
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("AnonymousInnerClassMayBeStatic")
@ExtendWith(WebTemplateTestExtension.class)
public class DrpBuilderTest extends AbstractWebTemplateTest {

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
    public void initialMedicationSafety() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Initial Medication Safety Report.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("initial_medication_safety_report/context/start_time", "2013-01-01T10:00:00.000+01:00")
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/reaction|code", "ac001")
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/reaction|value", "Value")
                .build();
        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode element = rawComposition.get("content").get(0).get("data").get("items").get(0).get("items").get(0).get("value");
        assertThat(element).isNotEmpty();
        assertThat(element.get("@class").asText()).isEqualTo("DV_CODED_TEXT");
        assertThat(element.get("defining_code").get("code_string").asText()).isEqualTo("ac001");
        assertThat(element.get("value").asText()).isEqualTo("Value");

        Map<String, Object> retrivedFlatComposition = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrivedFlatComposition).contains(
                entry("initial_medication_safety_report/medication_safety_event:0/adverse_effect:0/reaction|code", "ac001"),
                entry("initial_medication_safety_report/medication_safety_event:0/adverse_effect:0/reaction|value", "Value"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();

        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void initialMedicationSafety2() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Initial Medication Safety Report.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("initial_medication_safety_report/medication_safety_event/event_description", "Just some description !!")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode element = rawComposition.get("content").get(0).get("data").get("items").get(0).get("value");
        assertThat(element.get("@class").asText()).isEqualTo("DV_TEXT");
        assertThat(element.get("value").asText()).isEqualTo("Just some description !!");

        Map<String, Object> map = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(map).contains(
                entry("initial_medication_safety_report/medication_safety_event:0/event_description", "Just some description !!"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void initialMedicationSafety3() throws Exception {
        String template = getFileContent("/initial2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("initial_medication_safety_report/context/context_detail/period_of_care_identifier", "id")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void initialMedicationSafety4() throws Exception {
        String template = getFileContent("/initial2.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("initial_medication_safety_report/context/event_participant/participant_clinical_role", "at0.0.51")
                .put("initial_medication_safety_report/context/event_participant/participant_event_role", "at0.0.60")
                .put("initial_medication_safety_report/context/event_participant:1/participant_event_role", "at0.0.64")
                .put("initial_medication_safety_report/context/event_participant:1/participant_clinical_role", "at0.0.52")
                .put("initial_medication_safety_report/context/event_participant:2/participant_event_role", "at0.0.64")
                .put("initial_medication_safety_report/context/event_participant:2/participant_clinical_role", "at0.0.52")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition.get("context").get("other_context").get("items")).hasSize(3);

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void adverseReaction() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Adverse Drug Reaction Report.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("adverse_drug_reaction_report/adverse_drug_reaction/event_type", "at0250")
                .put("adverse_drug_reaction_report/adverse_drug_reaction/cause_of_event/estimated_cause_of_event", "at0067")
                .put("adverse_drug_reaction_report/adverse_drug_reaction/event_timestamp", DateTime.now())
                .put("adverse_drug_reaction_report/adverse_drug_reaction/cause_of_event/comment", "Comment")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void adverseReactionException() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Adverse Drug Reaction Report.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("adverse_drug_reaction_report/adverse_drug_reaction/event_type", "at0250")
                .put("adverse_drug_reaction_report/adverse_drug_reaction/cause_of_event/estimated_cause_of_event", "at0067")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void adverseReactionException2() throws Exception {
        String template = getFileContent("/adverse3.opt");
        String flatCompositionString = getFileContent("/adverse3.json");
        Map<String, Object> flatComposition = objectMapper.readValue(flatCompositionString, new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void errorReport() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Medication Error Report.opt");

        String flatCompositionString = getFileContent("/Error Report.json");
        Map<String, Object> flatComposition = objectMapper.readValue(flatCompositionString, new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void validationTest() throws Exception {
        String template = "ISPEK - MSE - Initial Medication Safety Report.opt";

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("initial_medication_safety_report/context/case_identifier", "10135201")
                .put("initial_medication_safety_report/context/context_detail/original_location|code", "44132-H")
                .put("initial_medication_safety_report/context/context_detail/original_location|value", "Endo-Hospital")
                .put("initial_medication_safety_report/context/context_detail/period_of_care_identifier", "29885508")
                .put("initial_medication_safety_report/context/event_participant/participant_clinical_role", "at0.0.53")
                .put("initial_medication_safety_report/context/event_participant/participant_event_role", "at0.0.60")
                .put("initial_medication_safety_report/context/event_participant:1/participant_clinical_role", "at0.0.87")
                .put("initial_medication_safety_report/context/event_participant:1/participant_event_role", "at0.0.63")
                .put("initial_medication_safety_report/context/status", "SAVED")
                .put("initial_medication_safety_report/medication_safety_event/actual_patient_outcome", "at0057")
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/reaction|code", "10")
                .put("initial_medication_safety_report/medication_safety_event/adverse_effect/severity", "at0224")
                .put("initial_medication_safety_report/medication_safety_event/event_timestamp", "2013-03-26T09:33:00.000+01:00")
                .put("initial_medication_safety_report/medication_safety_event/safety_event_type", "at0252")
                .build();

        assertThatThrownBy(() -> {
            getCompositionConverter().convertFlatToRaw(template, "si", objectMapper.writeValueAsString(flatComposition),
                                                       ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                                                       CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                                                       CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Composer"));
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void fixedValueTest() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Medication Event Case Summary.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("medication_event_case_summary/case_summary/patient_outcome_category", "at0064")
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|code", "R69")
                .put("medication_event_case_summary/context/case_identifier", 10118153L)
                .put("medication_event_case_summary/context/status", "IN_PROGRESS")
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|value",
                     "Neznani in neopredeljeni vzroki bolezni (MKB10AM)")
                .put("medication_event_case_summary/case_summary/actual_patient_outcome", "at0053")
                .put("medication_event_case_summary/case_summary/summary_details/discharge_diagnosis_classification|code", "E68")
                .put("medication_event_case_summary/case_summary/summary_details/discharge_diagnosis_classification|value",
                     "E68: Kasne posledice (sekvele) prenahranjenosti (MKB10AM)")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void fixedValueTestDouble() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Medication Event Case Summary.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("medication_event_case_summary/context/report_type", "at0.0.74")
                .put("medication_event_case_summary/case_summary/patient_outcome_category", "at0064")
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|code", "R69")
                .put("medication_event_case_summary/context/case_identifier", 10118153L)
                .put("medication_event_case_summary/context/status", "IN_PROGRESS")
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|value",
                     "Neznani in neopredeljeni vzroki bolezni (MKB10AM)")
                .put("medication_event_case_summary/case_summary/actual_patient_outcome", "at0053")
                .put("medication_event_case_summary/case_summary/summary_details/discharge_diagnosis_classification|code", "E68")
                .put("medication_event_case_summary/case_summary/summary_details/discharge_diagnosis_classification|value",
                     "E68: Kasne posledice (sekvele) prenahranjenosti (MKB10AM)")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
        assertThat(rawComposition.get("context").get("other_context").get("items")).hasSize(3);

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void elementWith2Values() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Medication Event Case Summary.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|code", "J00")
                .put("medication_event_case_summary/case_summary/patient_outcome_category|code", "at0064")
                .put("medication_event_case_summary/case_summary/patient_outcome_category|value", "Humanistični")
                .put("medication_event_case_summary/case_summary/patient_outcome_category:1|code", "at0065")
                .put("medication_event_case_summary/case_summary/patient_outcome_category:1|value", "Ekonomski")
                .put("medication_event_case_summary/context/status", "IN_PROGRESS")
                .put("medication_event_case_summary/context/case_identifier", "10168450")
                .put("medication_event_case_summary/case_summary/summary_details/admission_diagnosis_classification|value",
                     "Akutni nazofaringitis [navadni prehlad] (MKB10AM)")
                .put("medication_event_case_summary/case_summary/actual_patient_outcome", "at0053")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(
                entry("medication_event_case_summary/case_summary/patient_outcome_category:0|code", "at0064"),
                entry("medication_event_case_summary/case_summary/patient_outcome_category:1|code", "at0065"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void errorReportMultiple() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Medication Error Report.opt");

        String flatCompositionString = getFileContent("/Error Report 2.json");
        Map<String, Object> flatComposition = objectMapper.readValue(flatCompositionString, new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(
                entry("medication_error_report/medication_error/adverse_effect:0/intervention_details:0/intervention|code", "at0305"),
                entry("medication_error_report/medication_error/adverse_effect:0/intervention_details:1/intervention|code", "at0306"),
                entry("medication_error_report/medication_error/adverse_effect:1/intervention_details:0/intervention|code", "at0304"),
                entry("medication_error_report/medication_error/adverse_effect:1/intervention_details:1/intervention|code", "at0307"));

        JsonNode structuredComposition = getCompositionConverter().convertRawToStructured(template, "en", rawComposition.toString(), objectMapper);
        assertThat(structuredComposition).isNotNull();
        assertThat(getCompositionConverter().convertStructuredToRaw(
                template,
                "en",
                structuredComposition.toString(),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl"),
                objectMapper)).isNotNull();
    }

    @Test
    public void codedWithOther() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Drug Related Problem Report.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("drug_related_problem_report/medication_error/related_trigger:0", "at0271")
                .put("drug_related_problem_report/medication_error/related_trigger:1", "at0272")
                .put("drug_related_problem_report/medication_error/medra_classification|code", "10022117")
                .put("drug_related_problem_report/medication_error/additional_comment", "asdfasdfadsf")
                .put("drug_related_problem_report/medication_error/safety_event_type|value",
                     "Interakcija med zdravilom in boleznijo ali zdravilom in laboratorijskim izvidom.")
                .put("drug_related_problem_report/medication_error/patient_outcome_category:1|code", "at0065")
                .put("drug_related_problem_report/medication_error/related_trigger:2|other", "test")
                .put("drug_related_problem_report/medication_error/medra_classification|value", "10022117: Injury, poisoning and procedural complications")
                .put("drug_related_problem_report/medication_error/cause_of_event/estimated_cause_of_event", "at0076")
                .put("drug_related_problem_report/medication_error/actual_patient_outcome_details/actual_patient_outcome", "at0053")
                .put("drug_related_problem_report/medication_error/potential_patient_outcome", "at0056")
                .put("drug_related_problem_report/medication_error/safety_event_type|code", "at0262")
                .put("drug_related_problem_report/medication_error/patient_outcome_category|value", "Humanistični")
                .put("drug_related_problem_report/medication_error/patient_outcome_category:1|value", "Ekonomski")
                .put("drug_related_problem_report/medication_error/event_timestamp", "2015-03-04T13:42:00.000+01:00")
                .put("drug_related_problem_report/medication_error/patient_outcome_category|code", "at0064")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(
                entry("drug_related_problem_report/medication_error/related_trigger:0|code", "at0271"),
                entry("drug_related_problem_report/medication_error/related_trigger:1|code", "at0272"),
                entry("drug_related_problem_report/medication_error/related_trigger:2|other", "test"));
    }

    @Test
    public void codedWithOther1() throws Exception {
        String template = getFileContent("/ISPEK - MSE - Drug Related Problem Report.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("drug_related_problem_report/medication_error/related_trigger", "at0271")
                .put("drug_related_problem_report/medication_error/related_trigger|other", "test")
                .put("drug_related_problem_report/medication_error/medra_classification|code", "10022117")
                .put("drug_related_problem_report/medication_error/additional_comment", "asdfasdfadsf")
                .put("drug_related_problem_report/medication_error/safety_event_type|value",
                     "Interakcija med zdravilom in boleznijo ali zdravilom in laboratorijskim izvidom.")
                .put("drug_related_problem_report/medication_error/patient_outcome_category:1|code", "at0065")
                .put("drug_related_problem_report/medication_error/medra_classification|value", "10022117: Injury, poisoning and procedural complications")
                .put("drug_related_problem_report/medication_error/cause_of_event/estimated_cause_of_event", "at0076")
                .put("drug_related_problem_report/medication_error/actual_patient_outcome_details/actual_patient_outcome", "at0053")
                .put("drug_related_problem_report/medication_error/potential_patient_outcome", "at0056")
                .put("drug_related_problem_report/medication_error/safety_event_type|code", "at0262")
                .put("drug_related_problem_report/medication_error/patient_outcome_category|value", "Humanistični")
                .put("drug_related_problem_report/medication_error/patient_outcome_category:1|value", "Ekonomski")
                .put("drug_related_problem_report/medication_error/event_timestamp", "2015-03-04T13:42:00.000+01:00")
                .put("drug_related_problem_report/medication_error/patient_outcome_category|code", "at0064")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "Test"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(retrieve).contains(
                entry("drug_related_problem_report/medication_error/related_trigger:0|code", "at0271"),
                entry("drug_related_problem_report/medication_error/related_trigger:1|other", "test"));
    }
}