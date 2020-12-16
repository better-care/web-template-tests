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
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class WebTemplateContentTest extends AbstractWebTemplateTest {

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
    public void categoryReturned() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
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
        Map<String, Object> retrivedFlat = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrivedFlat).contains(entry("vitals/category|code", "433"));
        assertThat(retrivedFlat).contains(entry("vitals/category|terminology", "openehr"));
        assertThat(retrivedFlat).contains(entry("vitals/category|value", "event"));
    }

    @Test
    public void languageAndTerritoryReturned() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
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
        Map<String, Object> retrivedFlat = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);

        assertThat(retrivedFlat).contains(entry("vitals/language|code", "sl"));
        assertThat(retrivedFlat).contains(entry("vitals/territory|code", "SI"));
    }

    @Test
    public void contextEndTimeReturned() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer",
                        CompositionBuilderContextKey.START_TIME.getKey(), OffsetDateTime.of(2017, 11, 1, 1, 30, 0, 0, ZoneOffset.UTC),
                        CompositionBuilderContextKey.END_TIME.getKey(), OffsetDateTime.of(2017, 12, 1, 1, 30, 0, 0, ZoneOffset.UTC)),
                objectMapper);

        Map<String, Object> retrivedFlat = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrivedFlat).contains(entry("vitals/context/start_time", "2017-11-01T01:30:00Z"));
        assertThat(retrivedFlat).contains(entry("vitals/context/_end_time", "2017-12-01T01:30:00Z"));
    }

    @Test
    public void contextLocationReturned() throws Exception {
        String template = getFileContent("/res/Demo Vitals.opt");

        Map<String, Object> flatComposition = new ImmutableMap.Builder<String, Object>()
                .put("vitals/vitals/body_temperature/any_event/temperature|magnitude", 39.1)
                .put("vitals/vitals/body_temperature/any_event/temperature|unit", "°C")
                .put("vitals/vitals/body_temperature/any_event/body_exposure", "at0031")
                .build();

        Map<String, Object> context = new ImmutableMap.Builder<String, Object>()
                .put(CompositionBuilderContextKey.LANGUAGE.getKey(), "sl")
                .put(CompositionBuilderContextKey.TERRITORY.getKey(), "SI")
                .put(CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer")
                .put(CompositionBuilderContextKey.START_TIME.getKey(), OffsetDateTime.of(2017, 11, 1, 1, 30, 0, 0, ZoneOffset.UTC))
                .put(CompositionBuilderContextKey.END_TIME.getKey(), OffsetDateTime.of(2017, 12, 1, 1, 30, 0, 0, ZoneOffset.UTC))
                .put(CompositionBuilderContextKey.LOCATION.getKey(), "I am here!")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "sl",
                objectMapper.writeValueAsString(flatComposition),
                context,
                objectMapper);

        Map<String, Object> retrivedFlat = getCompositionConverter().convertRawToFlat(template, "sl", rawComposition.toString(), objectMapper);
        assertThat(retrivedFlat).contains(entry("vitals/context/start_time", "2017-11-01T01:30:00Z"));
        assertThat(retrivedFlat).contains(entry("vitals/context/_end_time", "2017-12-01T01:30:00Z"));
        assertThat(retrivedFlat).contains(entry("vitals/context/_location", "I am here!"));
    }
}
