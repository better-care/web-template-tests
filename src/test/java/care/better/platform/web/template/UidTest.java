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

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class UidTest extends AbstractWebTemplateTest {

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
    public void uid() throws Exception {
        String template = getFileContent("/res/clinical-summary-events.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("clinical_summary_events/context/setting|terminology", "openehr")
                        .put("clinical_summary_events/composer|name", "Dra. Amelia José")
                        .put("clinical_summary_events/documents/citation:0/citation-report/description", "FILE NAME TEST")
                        .put("clinical_summary_events/documents/citation:0/citation-report/report_date", "1970-01-01T00:00:00.000Z")
                        .put("clinical_summary_events/context/setting|238", "true")
                        .put("clinical_summary_events/context/setting|value", "other care")
                        .put("clinical_summary_events/documents/citation:0/_uid", "averyspecialuid")
                        .put("clinical_summary_events/_uid", "7c8de812-361a-4a08-b954-ebd9df0a15b8::default::1")
                        .put("clinical_summary_events/documents/citation:0/citation-report/report_category", "string")
                        .put("clinical_summary_events/context/start_time", "2015-09-29T09:07:29.273Z")
                        .put("clinical_summary_events/context/setting|code", "238")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition).isNotNull();

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("clinical_summary_events/documents/citation:0/_uid", "averyspecialuid"));
    }

    @Test
    public void compositionUid() throws Exception {
        String template = getFileContent("/res/clinical-summary-events.opt");

        Map<String, String> flatComposition =
                ImmutableMap.<String, String>builder()
                        .put("ctx/language", "sl")
                        .put("ctx/territory", "SI")
                        .put("ctx/id_scheme", "ispek")
                        .put("ctx/id_namespace", "ispek")
                        .put("ctx/composer_name", "George Orwell")
                        .put("clinical_summary_events/_uid", "compuid")
                        .put("clinical_summary_events/context/setting|terminology", "openehr")
                        .put("clinical_summary_events/composer|name", "Dra. Amelia José")
                        .put("clinical_summary_events/documents/citation:0/citation-report/description", "FILE NAME TEST")
                        .put("clinical_summary_events/documents/citation:0/citation-report/report_date", "1970-01-01T00:00:00.000Z")
                        .put("clinical_summary_events/context/setting|238", "true")
                        .put("clinical_summary_events/context/setting|value", "other care")
                        .put("clinical_summary_events/documents/citation:0/_uid", "averyspecialuid")
                        .put("clinical_summary_events/documents/citation:0/citation-report/report_category", "string")
                        .put("clinical_summary_events/context/start_time", "2015-09-29T09:07:29.273Z")
                        .put("clinical_summary_events/context/setting|code", "238")
                        .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("uid").get("value").asText()).isEqualTo("compuid");

        Map<String, Object> retrieved = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieved).contains(entry("clinical_summary_events/_uid", "compuid"));
        assertThat(retrieved).contains(entry("clinical_summary_events/documents/citation:0/_uid", "averyspecialuid"));
    }
}
