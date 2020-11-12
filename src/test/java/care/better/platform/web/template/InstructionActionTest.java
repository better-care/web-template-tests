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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * <p/>
 * Use cases:
 * 1. create a new composition with links to instructions within the same composition
 * always provides instruction_index, composition_uid NOT provided
 * -> resolve instruction uid from composition and set link appropriately
 * <p/>
 * 2. create a new composition with links to instructions in external compositions
 * always provides instruction_uid or instruction_index, composition_uid IS provided
 * -> fix path to use uid
 * <p/>
 * 3. update a composition which already has some links
 * instruction_uid and instruction_index both missing, composition_uid is present
 * -> apply all values as they are
 */

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class InstructionActionTest extends AbstractWebTemplateTest {

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
    public void instructionDetailsWithUUID() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")

                // action
                .put("medication_order/medication_detail/medication_action/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|instruction_uid", "insuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),

                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='insuid']")
        );
    }

    @Test
    public void singleInstructionDetails() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")

                // action
                .put("medication_order/medication_detail/medication_action/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),

//                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']")
        );
    }

    @Test
    public void multipleActivities() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order:0/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00")
                .put("medication_order/medication_detail/medication_instruction/order:1/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order:1/timing", "R2/2014-01-12T00:00:00.000+01:00")

                // action #1
                .put("medication_order/medication_detail/medication_action:0/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action:0/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action:0/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action:0/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action:0/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|activity_index", "0")

                // action #2
                .put("medication_order/medication_detail/medication_action:1/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action:1/ism_transition/current_state", "completed")
                .put("medication_order/medication_detail/medication_action:1/medicine|code", "b")
                .put("medication_order/medication_detail/medication_action:1/medicine|value", "Aspirin B")
                .put("medication_order/medication_detail/medication_action:1/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|activity_index", "1")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_instruction:0/order:1/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:1/timing", "R2/2014-01-12T00:00:00.000+01:00"),

//                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001,'Order']"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|activity_id", "activities[at0001,'Order #2']"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']")
        );
    }

    @Test
    public void multipleActivitiesPathAndIndex() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order:0/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00")
                .put("medication_order/medication_detail/medication_instruction/order:1/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order:1/timing", "R2/2014-01-12T00:00:00.000+01:00")

                // action #1
                .put("medication_order/medication_detail/medication_action:0/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action:0/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action:0/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action:0/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action:0/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "$selfComposition")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|activity_index", "0")

                // action #2
                .put("medication_order/medication_detail/medication_action:1/time", "2015-01-01T01:01:01.000Z")
                .put("medication_order/medication_detail/medication_action:1/ism_transition/current_state", "completed")
                .put("medication_order/medication_detail/medication_action:1/medicine|code", "b")
                .put("medication_order/medication_detail/medication_action:1/medicine|value", "Aspirin B")
                .put("medication_order/medication_detail/medication_action:1/instructions", "Take Aspirin as needed")

                // action.instruction_details - activity_id is no longer needed - it's taken from the template
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|composition_uid", "$selfComposition")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|activity_index", "1")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.ACTION_TO_INSTRUCTION_HANDLER.getKey(), true
                        ),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_instruction:0/order:1/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:1/timing", "R2/2014-01-12T00:00:00.000+01:00"),

//                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001,'Order']"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|activity_id", "activities[at0001,'Order #2']"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction']")
        );
    }

    @Test
    public void action() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // action
                .put("medication_order/medication_detail/medication_action:0/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action:0/medicine|code", "aspirin")
                .put("medication_order/medication_detail/medication_action:0/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action:0/instructions", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "cuid")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|instruction_uid", "iuid1")
                .put("medication_order/medication_detail/medication_action:0/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")

                .put("medication_order/medication_detail/medication_action:1/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action:1/medicine|code", "medrol")
                .put("medication_order/medication_detail/medication_action:1/medicine|value", "Medrol")
                .put("medication_order/medication_detail/medication_action:1/instructions", "Take Medrol as needed")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|composition_uid", "cuid")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|instruction_uid", "iuid2")
                .put("medication_order/medication_detail/medication_action:1/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "cuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='iuid1']"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|composition_uid", "cuid"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:1/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='iuid2']")
        );
    }

    @Test
    public void instructionDetailsWithUUIDOnRmPath() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")

                // action
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|instruction_uid", "insuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and name/value='Medication instruction']")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),

                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='insuid']")
        );
    }

    @Test
    public void instructionDetailsWithIndex() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")

                // action
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|instruction_index", "2")
                .put("medication_order/medication_detail/medication_action/_instruction_details|wt_path",
                     "medication_order/medication_detail/medication_instruction")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #3']")
        );
    }

    @Test
    public void instructionDetailsWithIndexOnRmPath() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")

                // action
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|instruction_index", "2")
                .put("medication_order/medication_detail/medication_action/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #17']")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #3']")
        );
    }

    @Test
    public void instructionDetailsWithExistingUidPath() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")

                // action
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='abc']")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1 and uid/value='abc']")
        );
    }

    @Test
    public void instructionDetailsWithExistingIndexedPath() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")

                // action
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #37']")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #37']")
        );
    }

    @Test
    public void instructionDetailsWithExistingInstructionUid() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/_uid", "insuid")
                .put("medication_order/medication_detail/medication_instruction/narrative", "Take Aspirin as needed")
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")
                .put("medication_order/medication_detail/medication_instruction/order/timing", "R3/2014-01-10T00:00:00.000+01:00")
                .put("medication_order/medication_detail/medication_action/ism_transition/current_state", "524")

                // action
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                // action.instruction_details
                .put("medication_order/medication_detail/medication_action/_instruction_details|activity_id", "activities[at0001]")
                .put("medication_order/medication_detail/medication_action/_instruction_details|composition_uid", "compositionuid")
                .put("medication_order/medication_detail/medication_action/_instruction_details|path",
                     "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #37']")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(
                entry("medication_order/medication_detail/medication_instruction:0/_uid", "insuid"),
                entry("medication_order/medication_detail/medication_instruction:0/narrative", "Take Aspirin as needed"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/medicine", "Aspirin"),
                entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R3/2014-01-10T00:00:00.000+01:00"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|composition_uid", "compositionuid"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|activity_id", "activities[at0001]"),
                entry("medication_order/medication_detail/medication_action:0/_instruction_details|path",
                      "/content[openEHR-EHR-SECTION.medication.v1,'Medication detail']/items[openEHR-EHR-INSTRUCTION.medication.v1,'Medication instruction #37']")
        );
    }

    // todo speed test? Grouped as manual

    @Test
    public void instructionNoTimingNarrative() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_instruction:0/narrative", "<none>"));
        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R1"));
    }

    @Test
    public void instructionNoTimingNarrativeCtx() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("ctx/instruction_narrative", "Hello world!")
                .put("ctx/activity_timing", "R7")

                // instruction
                .put("medication_order/medication_detail/medication_instruction/order/medicine", "Aspirin")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_instruction:0/narrative", "Hello world!"));
        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_instruction:0/order:0/timing", "R7"));
    }

    @Test
    public void actionNoISMCurrentState() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")

                // action
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_action:0/ism_transition/current_state|value", "completed"));
    }

    @Test
    public void actionNoISMCurrentStateCtx() throws Exception {
        String template = getFileContent("/res/ISPEK - MED - Medication Order.opt");
        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("ctx/action_ism_transition_current_state", "active")

                // action
                .put("medication_order/medication_detail/medication_action/medicine|code", "a")
                .put("medication_order/medication_detail/medication_action/medicine|value", "Aspirin")
                .put("medication_order/medication_detail/medication_action/instructions", "Take Aspirin as needed")

                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrieved).contains(entry("medication_order/medication_detail/medication_action:0/ism_transition/current_state|value", "active"));
    }

    @Test
    public void emptyActivity() throws Exception {
        String template = getFileContent("/res/ISPEK - ZN - Restraint.opt");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/restraint.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void emptyInstruction() throws Exception {
        String template = getFileContent("/res/Melanoma ST.xml");

        JsonNode structuredComposition = objectMapper.readTree(getFileContent("/res/melanoma.json"));

        JsonNode rawComposition = getCompositionConverter().convertStructuredToRaw(
                template,
                "sl",
                structuredComposition.toString(),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void instructionDetailsWithMultipleActivities() throws Exception {
        String templateRequest = getFileContent("/res/Service Request.xml");
        String templateService = getFileContent("/res/Service.xml");
        Map<String, Object> flatCompositionRequest = objectMapper.readValue(getFileContent("/res/service_request.json"), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> flatCompositionService = objectMapper.readValue(getFileContent("/res/service.json"), new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawCompositionService = getCompositionConverter().convertFlatToRaw(
                templateService,
                "sl",
                objectMapper.writeValueAsString(flatCompositionService),
                Collections.emptyMap(),
                objectMapper);

        JsonNode rawCompositionRequest = getCompositionConverter().convertFlatToRaw(
                templateRequest,
                "sl",
                objectMapper.writeValueAsString(flatCompositionRequest),
                Collections.emptyMap(),
                objectMapper);

        assertThat(getCompositionValidator().validate(templateRequest, rawCompositionRequest.toString())).isEmpty();
        assertThat(getCompositionValidator().validate(templateService, rawCompositionService.toString())).isEmpty();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(templateService, "sl", rawCompositionService.toString(), objectMapper);
        assertThat(retrieved).contains(
                entry("service/service:0/_instruction_details|activity_id", "activities[at0001, 'Request']"),
                entry("service/service:1/_instruction_details|activity_id", "activities[at0001, 'Request #2']")
        );
    }

    @Test
    public void invalidState() throws Exception {
        String template = getFileContent("/res/openEHR-EHR-COMPOSITION.t_suspension_drug_therapy.v1.xml");
        Map<String, Object> faltComposition = objectMapper.readValue(getFileContent("/res/compositionToSave1.json"), new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "ru",
                objectMapper.writeValueAsString(faltComposition),
                Collections.emptyMap(),
                objectMapper);
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }

    @Test
    public void wfDefinition() throws Exception {
        String template = getFileContent("/res/Service Request.xml");
        Map<String, Object> flatComposition1 = new ImmutableMap.Builder<String, Object>()
                .put("ctx/language", "en")
                .put("ctx/territory", "US")
                .put("ctx/composer_name", "tester")
                .put("service_request/service_request:0/request:0/service_name", "Walk a mile")
                .put("service_request/service_request:0/request:1/service_name", "Swim")
                .put("service_request/service_request:0/request:2/service_name", "Drink water")
                .put("service_request/service_request:0/_wf_definition", "Hello world")
                .put("service_request/service_request:0/_wf_definition|formalism", "myown")
                .build();
        Map<String, Object> flatComposition2 = new ImmutableMap.Builder<String, Object>()
                .put("ctx/language", "en")
                .put("ctx/territory", "US")
                .put("ctx/composer_name", "tester")
                .put("service_request/service_request:0/request:0/service_name", "Walk a mile")
                .put("service_request/service_request:0/request:1/service_name", "Swim")
                .put("service_request/service_request:0/request:2/service_name", "Drink water")
                .put("service_request/service_request:0/_wf_definition|value", "Hello world")
                .put("service_request/service_request:0/_wf_definition|formalism", "xxx")
                .build();

        JsonNode rawComposition1 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition1),
                Collections.emptyMap(),
                objectMapper);

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper);


        assertThat(getCompositionValidator().validate(template, rawComposition1.toString())).isEmpty();
        assertThat(getCompositionValidator().validate(template, rawComposition2.toString())).isEmpty();

        assertThat(rawComposition1.get("content").get(0).get("wf_definition").get("value").asText()).isEqualTo("Hello world");
        assertThat(rawComposition1.get("content").get(0).get("wf_definition").get("formalism").asText()).isEqualTo("myown");
        assertThat(rawComposition2.get("content").get(0).get("wf_definition").get("value").asText()).isEqualTo("Hello world");
        assertThat(rawComposition2.get("content").get(0).get("wf_definition").get("formalism").asText()).isEqualTo("xxx");

        Map<String, Object> retrieved1 = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition1.toString(), objectMapper);
        assertThat(retrieved1).contains(
                entry("service_request/service_request:0/_wf_definition|value", "Hello world"),
                entry("service_request/service_request:0/_wf_definition|formalism", "myown"));

        Map<String, Object> retrieved2 = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition2.toString(), objectMapper);
        assertThat(retrieved2).contains(
                entry("service_request/service_request:0/_wf_definition|value", "Hello world"),
                entry("service_request/service_request:0/_wf_definition|formalism", "xxx"));

        JsonNode retrieveJson1 = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition1.toString(), objectMapper);
        JsonNode wfDefNode1 = retrieveJson1.path("service_request").path("service_request").path(0).path("_wf_definition").path(0);
        assertThat(wfDefNode1.path("|value").textValue()).isEqualTo("Hello world");
        assertThat(wfDefNode1.path("|formalism").textValue()).isEqualTo("myown");

        JsonNode retrieveJson2 = getCompositionConverter().convertRawToStructured(template, "sl", rawComposition2.toString(), objectMapper);
        JsonNode wfDefNode2 = retrieveJson2.path("service_request").path("service_request").path(0).path("_wf_definition").path(0);
        assertThat(wfDefNode2.path("|value").textValue()).isEqualTo("Hello world");
        assertThat(wfDefNode2.path("|formalism").textValue()).isEqualTo("xxx");
    }
}
