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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class WorkflowIdTest extends AbstractWebTemplateTest {

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
    public void workflowIdDirect() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        OffsetDateTime dateTime = ZonedDateTime.of(2015, 1, 1, 10, 31, 16, 0, ZoneId.systemDefault()).toOffsetDateTime();
        Map<String, String> flatComposition = 
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/composer_name", "Composer")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/end_time", "2016-01-01T12:30:30Z")
                        .put("vitals/vitals/haemoglobin_a1c/history_origin", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime))
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .put("vitals/vitals/haemoglobin_a1c/_work_flow_id|id", "1")
                        .put("vitals/vitals/haemoglobin_a1c/_work_flow_id|id_scheme", "x")
                        .put("vitals/vitals/haemoglobin_a1c/_work_flow_id|namespace", "y")
                        .put("vitals/vitals/haemoglobin_a1c/_work_flow_id|type", "wf")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        JsonNode workFlowId = rawComposition.get("content").get(0).get("items").get(0).get("work_flow_id");
        assertThat(workFlowId.get("id").get("@class").asText()).isEqualTo("GENERIC_ID");
        assertThat(workFlowId.get("id").get("value").asText()).isEqualTo("1");
        assertThat(workFlowId.get("namespace").asText()).isEqualTo("y");
    }

    @Test
    public void workflowIdInCtx() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = 
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/composer_name", "Composer")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/end_time", "2016-01-01T12:30:30Z")
                        .put("ctx/work_flow_id|id", "wf_id")
                        .put("ctx/work_flow_id|namespace", "wf_ns")
                        .put("ctx/work_flow_id|id_scheme", "wf_scheme")
                        .put("ctx/work_flow_id|type", "wf_type")
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

        JsonNode workFlowId = rawComposition.get("content").get(0).get("items").get(0).get("work_flow_id");
        assertThat(workFlowId).isNotNull();
        assertThat(workFlowId.get("id").get("@class").asText()).isEqualTo("GENERIC_ID");
        assertThat(workFlowId.get("id").get("value").asText()).isEqualTo("wf_id");
        assertThat(workFlowId.get("namespace").asText()).isEqualTo("wf_ns");
        assertThat(workFlowId.get("type").asText()).isEqualTo("wf_type");
    }

    @Test
    public void workflowIdInCtxAndDirect() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, String> flatComposition = 
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/composer_name", "Composer")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/end_time", "2016-01-01T12:30:30Z")
                        .put("ctx/work_flow_id|id", "wf_id")
                        .put("ctx/work_flow_id|namespace", "wf_ns")
                        .put("ctx/work_flow_id|id_scheme", "wf_scheme")
                        .put("ctx/work_flow_id|type", "wf_type")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c/any_event/test_status|code", "at0037")
                        .put("vitals/vitals/haemoglobin_a1c:1/any_event/test_status|terminology", "local")
                        .put("vitals/vitals/haemoglobin_a1c:1/any_event/test_status|code", "at0037")
                        .put("vitals/vitals/haemoglobin_a1c:1/_work_flow_id|id", "1")
                        .put("vitals/vitals/haemoglobin_a1c:1/_work_flow_id|id_scheme", "x")
                        .put("vitals/vitals/haemoglobin_a1c:1/_work_flow_id|namespace", "y")
                        .put("vitals/vitals/haemoglobin_a1c:1/_work_flow_id|type", "wf")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        

        JsonNode observation1 = rawComposition.get("content").get(0).get("items").get(0);
        assertThat(observation1.get("work_flow_id")).isNotNull();
        assertThat(observation1.get("work_flow_id").get("id").get("@class").asText()).isEqualTo("GENERIC_ID");
        assertThat(observation1.get("work_flow_id").get("id").get("value").asText()).isEqualTo("wf_id");
        assertThat(observation1.get("work_flow_id").get("namespace").asText()).isEqualTo("wf_ns");
        assertThat(observation1.get("work_flow_id").get("type").asText()).isEqualTo("wf_type");

        JsonNode observation2 = rawComposition.get("content").get(0).get("items").get(1);
        assertThat(observation2.get("work_flow_id")).isNotNull();
        assertThat(observation2.get("work_flow_id").get("id").get("@class").asText()).isEqualTo("GENERIC_ID");
        assertThat(observation2.get("work_flow_id").get("id").get("value").asText()).isEqualTo("1");
        assertThat(observation2.get("work_flow_id").get("namespace").asText()).isEqualTo("y");
        assertThat(observation2.get("work_flow_id").get("type").asText()).isEqualTo("wf");
    }
}
