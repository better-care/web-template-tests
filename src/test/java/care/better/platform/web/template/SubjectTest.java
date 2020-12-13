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
                objectMapper
        );

        getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

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
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );

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
                objectMapper
        );

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(retrieved).contains(
                entry("vitals/vitals/haemoglobin_a1c:0/subject|name", "Marija Medved"),
                entry("vitals/vitals/haemoglobin_a1c:0/subject|id", "998")
        );

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
                objectMapper
        );

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
                objectMapper
        );

        ObjectNode node = (ObjectNode)rawComposition.get("content").get(0).get("items").get(0);
        node.set("subject", null);

        Map<String, Object> retrieved1 = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(retrieved1.keySet()).doesNotContain("vitals/vitals/haemoglobin_a1c:0/subject|name", "vitals/vitals/haemoglobin_a1c:0/subject|id");
        node.set("subject", objectMapper.readTree("{\"@class\":\"PARTY_IDENTIFIED\",\"external_ref\":null,\"name\":null,\"identifiers\":[]}"));

        Map<String, Object> retrieved2 = getCompositionConverter().convertRawToFlat(
                template,
                "sl",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(retrieved2.keySet()).doesNotContain("vitals/vitals/haemoglobin_a1c:0/subject|name", "vitals/vitals/haemoglobin_a1c:0/subject|id");
    }

    @Test
    public void retrievedTest() throws Exception {
        String template = getFileContent("/res/clinical-summary-events.opt");

        ImmutableMap<String, Object> flatComposition =
                ImmutableMap.<String, Object>builder()
                        .put("ctx/language", "pt")
                        .put("ctx/territory", "BR")
                        .put("ctx/composer_name", "User")
                        .put("clinical_summary_events/_uid", "75bab263-a9d4-4522-b265-bada4b298f56::bostjanl::1")
                        .put("clinical_summary_events/context/start_time", "2015-10-05T10:26:18.000Z")
                        .put("clinical_summary_events/context/setting|", true)
                        .put("clinical_summary_events/context/setting|code", "")
                        .put("clinical_summary_events/context/setting|value", "")
                        .put("clinical_summary_events/context/setting|terminology", "")
                        .put("clinical_summary_events/episodes/admission/patient_admission/patient_class", "Pronto socorro")
                        .put("clinical_summary_events/episodes/admission/patient_admission/attending_doctor/id_issuer", "CRM-SP")
                        .put("clinical_summary_events/episodes/admission/patient_admission/referring_doctor/id_issuer", "CRM-SP")
                        .put("clinical_summary_events/episodes/admission/patient_admission/consulting_doctor/id", "39")
                        .put("clinical_summary_events/episodes/admission/patient_admission/consulting_doctor/name", "Drª Margarida Martins")
                        .put("clinical_summary_events/episodes/admission/patient_admission/consulting_doctor/id_issuer", "CRM-SP")
                        .put("clinical_summary_events/episodes/admission/patient_admission/admitting_doctor/id", "39")
                        .put("clinical_summary_events/episodes/admission/patient_admission/admitting_doctor/name", "Drª Margarida Martins")
                        .put("clinical_summary_events/episodes/admission/patient_admission/admitting_doctor/id_issuer", "CRM-SP")
                        .put("clinical_summary_events/episodes/admission/patient_admission/admit_date_time", "2013-11-19T17:00:00.000Z")
                        .put("clinical_summary_events/episodes/admission/patient_admission/readmission", false)
                        .put("clinical_summary_events/episodes/reason_for_encounter/reason_for_encounter:0/_uid", "d083c3e3-8403-48fe-8bee-927dbc641f07")
                        .put("clinical_summary_events/episodes/reason_for_encounter/reason_for_encounter:0/_provider|name", "Drª Margarida Martins")
                        .put("clinical_summary_events/episodes/reason_for_encounter/reason_for_encounter:0/presenting_problem", "Choque anafilático")
                        .put("clinical_summary_events/episodes/reason_for_encounter/reason_for_encounter:0/registration_date", "2013-11-19T17:00:00.000Z")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/_uid",
                             "33f205ad-7da7-44cc-92d0-85e4618acf64")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/_provider|name",
                             "CHS Admin")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/substance_agent",
                             "Antibacterianos Beta-Lactâmicos, Penicilinas")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/absolute_contraindication",
                             false)
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/overall_comment",
                             "")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/specific_substance_agent",
                             "Amoxicilina")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/manifestation:0",
                             "Rash, Urticária")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/reaction_type",
                             "Alergia")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/certainty",
                             "Confirmado")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/reaction_description",
                             "Esta é uma reação que aconteceu tardiamente, depois de várias administrações durante a vida da paciente nas quais não houve qualquer tipo de reação adversa manifestada.")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/reaction_event/onset_of_reaction",
                             "2003-11-12T00:00:00.000Z")
                        .put("clinical_summary_events/alergies_adverse_reactions_and_intolerances/allergies_and_adverse_reactions/adverse_reaction:0/registration_date",
                             "2003-11-19T17:00:00.000Z")
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/_uid", "8eaf393d-94ac-485d-8058-e5641935f7ad")
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/_provider|name", "Drª Margarida Martins")
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/any_event:0/systolic|magnitude", 136)
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/any_event:0/systolic|unit", "mm[Hg]")
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/any_event:0/diastolic|magnitude", 81)
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/any_event:0/diastolic|unit", "mm[Hg]")
                        .put("clinical_summary_events/vital_signs/blood_pressure/blood_pressure:0/any_event:0/time", "2013-11-19T17:00:00.000Z")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }
}
