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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class SettersTest extends AbstractWebTemplateTest {

    private ObjectMapper objectMapper;
    private Map<String, Object> context;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        context = ImmutableMap.of(
                CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer");
    }

    @Test
    public void testFixedValues() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(ImmutableMap.of("testing_template/context/testing/fixed_values/fixed_text|code", "at0009")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(
                entry("testing_template/context/testing/fixed_values/fixed_text|code", "at0008"),
                entry("testing_template/context/testing/fixed_values/fixed_ordinal|code", "at0009"),
                entry("testing_template/context/testing/fixed_values/fixed_ordinal|ordinal", 1),
                entry("testing_template/context/testing/fixed_values/fixed_count", 1),
                entry("testing_template/context/testing/fixed_values/fixed_boolean", true));
    }

    @Test
    public void testMultimedia() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/multimedia", "http://here.com/123",
                                "testing_template/context/testing/multimedia|alternatetext", "Hello world!",
                                "testing_template/context/testing/multimedia|mediatype", "png",
                                "testing_template/context/testing/multimedia|size", "999")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(
                entry("testing_template/context/testing/multimedia", "http://here.com/123"),
                entry("testing_template/context/testing/multimedia|alternatetext", "Hello world!"),
                entry("testing_template/context/testing/multimedia|mediatype", "png"),
                entry("testing_template/context/testing/multimedia|size", 999));
    }

    @Test
    public void testMultimediaFailed1() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/multimedia", "http://here.com/123",
                                "testing_template/context/testing/multimedia|xyz", "Hello world!")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unknown attribute 'xyz' for DV_MULTIMEDIA");
    }

    @Test
    public void testMultimediaFailed2() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/multimedia", "http://here.com/123",
                                "testing_template/context/testing/multimedia|size", "XYZ")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid value for attribute 'size' of DV_MULTIMEDIA (numeric expected): XYZ");
    }

    @Test
    public void testProportion() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/proportion|numerator", "10",
                                "testing_template/context/testing/proportion|denominator", "100")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(
                entry("testing_template/context/testing/proportion|numerator", 10.0),
                entry("testing_template/context/testing/proportion|denominator", 100.0));
    }

    @Test
    public void testProportionFailed1() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/proportion|numerator", "xyz",
                                "testing_template/context/testing/proportion|denominator", "100")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid decimal value: xyz (path: testing_template/context/testing/proportion|numerator)");
    }

    @Test
    public void testProportionFailed2() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of(
                                "testing_template/context/testing/proportion|numerator", "10",
                                "testing_template/context/testing/proportion|denominator", "abc")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid decimal value: abc (path: testing_template/context/testing/proportion|denominator)");
    }

    @Test
    public void testUri() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/uri", "http://www.google.com")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("testing_template/context/testing/uri", "http://www.google.com"));
    }

    @Test
    public void testDate1() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", "2014-1-13")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", new LocalDate(2014, 1, 13))),
                context,
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        JsonNode rawComposition3 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", new DateTime(2014, 1, 13, 10, 13))),
                context,
                objectMapper);

        Map<String, Object> formatted3 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition3.toString(),
                objectMapper);

        assertThat(formatted3).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", true)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Error processing form value: true (path: testing_template/context/testing/date)");
    }

    @Test
    public void testDate2() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", "2014-1-13")),
                context,
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", new LocalDate(2014, 1, 13))),
                context,
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        JsonNode rawComposition3 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", new DateTime(2014, 1, 13, 10, 13))),
                context,
                objectMapper);

        Map<String, Object> formatted3 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition3.toString(),
                objectMapper);

        assertThat(formatted3).contains(entry("testing_template/context/testing/date", "2014-01-13"));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date", "2014-a-b")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Error processing form value: 2014-a-b (path: testing_template/context/testing/date)");
    }

    @Test
    public void testTime1() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", "14:35")),
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

        assertThat(formatted).contains(entry("testing_template/context/testing/time", "14:35:00"));

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", java.time.LocalTime.of(14, 35, 10, 117000000))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/time", "14:35:10.117"));

        JsonNode rawComposition3 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", ZonedDateTime.of(2014, 1, 13, 14, 35, 10, 117000000, ZoneId.systemDefault()))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted3 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition3.toString(),
                objectMapper);

        assertThat(formatted3).contains(entry("testing_template/context/testing/time", "14:35:10.117+01:00"));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", true)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unable to convert value to LocalTime: true (path: testing_template/context/testing/time)");
    }

    @Test
    public void testTime() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", "14:35")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("testing_template/context/testing/time", "14:35:00"));
    }

    @Test
    public void testTime2() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", "14:35")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        assertThat(formatted).contains(entry("testing_template/context/testing/time", "14:35:00"));

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", new LocalTime(14, 35, 10, 117))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/time", "14:35:10.117"));

        JsonNode rawComposition3 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time",  ISODateTimeFormat.dateTime().print(new DateTime(2014, 1, 13, 14, 35, 10, 117)))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted3 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition3.toString(),
                objectMapper);

        assertThat(formatted3).contains(entry("testing_template/context/testing/time", "14:35:10.117+01:00"));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/time", "17:aa")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unable to convert value to LocalTime: 17:aa (path: testing_template/context/testing/time)");
    }

    @Test
    public void testDateTime1() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", "2014-1-13T14:35:00.000")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        String formatted = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.of(2014, 1, 13, 14, 35, 0, 0, ZoneId.systemDefault()));
        assertThat(formatted1).contains(entry("testing_template/context/testing/date_time", formatted));

        DateTime dt = new DateTime(2014, 1, 13, 14, 35, 10, 117);

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", ISODateTimeFormat.dateTime().print(dt))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/date_time", ISODateTimeFormat.dateTime().print(dt)));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", true)),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unable to convert value to datetime: true (path: testing_template/context/testing/date_time)");
    }

    @Test
    public void testDateTime2() throws Exception {
        String template = getFileContent("/res/Testing Template.opt");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", "2014-1-13T14:35:00.000")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper);

        String formatted = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.of(2014, 1, 13, 14, 35, 0, 0, ZoneId.systemDefault()));
        assertThat(formatted1).contains(entry("testing_template/context/testing/date_time", formatted));

        DateTime dt = new DateTime(2014, 1, 13, 14, 35, 10, 117);

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", ISODateTimeFormat.dateTime().print(dt))),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        Map<String, Object> formatted2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper);

        assertThat(formatted2).contains(entry("testing_template/context/testing/date_time", ISODateTimeFormat.dateTime().print(dt)));

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(
                        ImmutableMap.of("testing_template/context/testing/date_time", "17:aa")),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "sl",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "SI",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Unable to convert value to DateTime: 17:aa (path: testing_template/context/testing/date_time)");
    }
}
