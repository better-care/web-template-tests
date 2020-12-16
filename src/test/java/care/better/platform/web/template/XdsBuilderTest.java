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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class XdsBuilderTest extends AbstractWebTemplateTest {

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
    public void cdaDocument() throws Exception {
        String template = getFileContent("/res/CDA Document.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("cda_document/context/setting|code", "238")
                .put("cda_document/context/setting|value", "other care")
                .put("cda_document/xds_metadata/mime_type", "text/xml")
                .put("cda_document/xds_metadata/creation_time", DateTime.now())
                .put("cda_document/xds_metadata/title", "Document name")
                .put("cda_document/xds_metadata/class|code", "class_code")
                .put("cda_document/xds_metadata/class|value", "class_value")
                .put("cda_document/xds_metadata/format|code", "format_value")
                .put("cda_document/xds_metadata/format|value", "format_value")
                .put("cda_document/xds_metadata/practice_setting|code", "practice_value")
                .put("cda_document/xds_metadata/practice_setting|value", "practice_value")
                .put("cda_document/xds_metadata/type|code", "type_code")
                .put("cda_document/xds_metadata/type|value", "type_value")
                .put("cda_document/xds_metadata/event:0|code", "event1_code")
                .put("cda_document/xds_metadata/event:0|value", "event1_value")
                .put("cda_document/xds_metadata/event:1|code", "event2_code")
                .put("cda_document/xds_metadata/event:1|value", "event2_value")
                .put("cda_document/cda_component:0/name", "name1")
                .put("cda_document/cda_component:0/templateid", "1.3.6.1.4.1.19376.1.5.3.1.3.3")
                .put("cda_document/cda_component:0/code|code", "46241-6")
                .put("cda_document/cda_component:0/code|value", "HOSPITAL ADMISSION DX")
                .put("cda_document/cda_component:0/title", "2. Aktivni zdravstveni problemi:")
                .put("cda_document/cda_component:0/text", "Osteoartroza in TEP kolena leta 2000.")
                .put("cda_document/cda_component:0/text|formalism", "text/html")
                .put("cda_document/cda_component:1/name", "name2")
                .put("cda_document/cda_component:1/templateid", "1.3.6.1.4.1.19376.1.5.3.1.3.3")
                .put("cda_document/cda_component:1/code|code", "46241-6")
                .put("cda_document/cda_component:1/code|value", "HOSPITAL ADMISSION DX")
                .put("cda_document/cda_component:1/title", "2. Aktivni zdravstveni problemi:")
                .put("cda_document/cda_component:1/text", "Osteoartroza in TEP kolena leta 2000.")
                .put("cda_document/cda_component:1/text|formalism", "text/html")
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
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        Map<String, Object> retrieve = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(retrieve.get("cda_document/xds_metadata/event:0|code")).isEqualTo("event1_code");
        assertThat(retrieve.get("cda_document/xds_metadata/event:1|code")).isEqualTo("event2_code");
        assertThat(retrieve.get("cda_document/cda_component:0/templateid")).isEqualTo("1.3.6.1.4.1.19376.1.5.3.1.3.3");
    }

    @Test
    public void xdsGenericDocument() throws Exception {
        String template = getFileContent("/res/XDS Document.opt");

        Map<String, Object> flatComposition = ImmutableMap.<String, Object>builder()
                .put("ctx/health_care_facility|name", "hospital")
                .put("ctx/health_care_facility|id", "hospital id")
                .put("ctx/language", "en")
                .put("xds_document/context/setting|code", "238")
                .put("xds_document/context/setting|value", "other care")
                .put("xds_document/context/start_time", DateTime.now())
                .put("xds_document/context/end_time", DateTime.now())
                .put("xds_document/xds_metadata/mime_type", "text/xml")
                .put("xds_document/xds_metadata/creation_time", DateTime.now())
                .put("xds_document/xds_metadata/title", "Document name")
                .put("xds_document/xds_metadata/class|code", "class_code")
                .put("xds_document/xds_metadata/class|value", "class_value")
                .put("xds_document/xds_metadata/format|code", "format_value")
                .put("xds_document/xds_metadata/format|value", "format_value")
                .put("xds_document/xds_metadata/practice_setting|code", "practice_value")
                .put("xds_document/xds_metadata/practice_setting|value", "practice_value")
                .put("xds_document/xds_metadata/type|code", "type_code")
                .put("xds_document/xds_metadata/type|value", "type_value")
                .put("xds_document/xds_metadata/event:0|code", "event1_code")
                .put("xds_document/xds_metadata/event:0|value", "event1_value")
                .put("xds_document/xds_metadata/event:1|code", "event2_code")
                .put("xds_document/xds_metadata/event:1|value", "event2_value")
                .put("xds_document/xds_metadata/author/author_person", "Jim Smith")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.ID_NAMESPACE.getKey(), "y",
                        CompositionBuilderContextKey.ID_SCHEME.getKey(), "x"),
                objectMapper);

        assertThat(rawComposition).isNotNull();
        assertThat(rawComposition.get("context").get("health_care_facility").get("name").asText()).isEqualTo("hospital");
        assertThat(rawComposition.get("context").get("health_care_facility").get("external_ref").get("id").get("value").asText()).isEqualTo("hospital id");
        assertThat(rawComposition.get("context").get("end_time")).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();
    }
}
