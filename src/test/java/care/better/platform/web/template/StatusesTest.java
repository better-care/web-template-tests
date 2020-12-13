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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class StatusesTest extends AbstractWebTemplateTest {

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
    public void ordinal() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/ordinal", "1")
                .put("test_statuses/test_statuses:0/ordinal|normal_status", "L")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(rawComposition).isNotNull();

        JsonNode ordinal1 = getDataValue(rawComposition);

        assertThat(ordinal1.get("normal_status").get("code_string").asText()).isEqualTo("L");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/ordinal|normal_status", "L"));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/ordinal", "1")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(rawComposition2).isNotNull();

        JsonNode ordinal2 = getDataValue(rawComposition2);

        assertThat(ordinal2.get("normal_status")).isInstanceOf(NullNode.class);

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );

        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
    }

    @Test
    public void ordinalN() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/ordinal", "1")
                .put("test_statuses/test_statuses:0/ordinal|normal_status", "N")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );

        assertThat(rawComposition).isNotNull();
        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

        JsonNode ordinal1 = getDataValue(rawComposition);

        assertThat(ordinal1.get("normal_status").get("code_string").asText()).isEqualTo("N");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/ordinal|normal_status", "N"));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/ordinal", "1")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode ordinal2 = getDataValue(rawComposition2);

        assertThat(ordinal2.get("normal_status")).isInstanceOf(NullNode.class);

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );

        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
    }

    @Test
    public void ordinalInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/ordinal", "1")
                .put("test_statuses/test_statuses:0/ordinal|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        ))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");
    }

    @Test
    public void duration() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/duration", "P1Y")
                .put("test_statuses/test_statuses:0/duration|normal_status", "L")
                .put("test_statuses/test_statuses:0/duration|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();

        JsonNode duration1 = getDataValue(rawComposition);

        assertThat(duration1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(duration1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );

        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/duration|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/duration|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/duration", "P1M")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode duration2 = getDataValue(rawComposition2);

        assertThat(duration2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(duration2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void durationInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/duration", "P1Y")
                .put("test_statuses/test_statuses:0/duration|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/duration", "P1Y")
                .put("test_statuses/test_statuses:0/duration|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void quantity() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/quantity|unit", "m")
                .put("test_statuses/test_statuses:0/quantity|magnitude", "1")
                .put("test_statuses/test_statuses:0/quantity|normal_status", "L")
                .put("test_statuses/test_statuses:0/quantity|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();

        JsonNode quantity1 = getDataValue(rawComposition);

        assertThat(quantity1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(quantity1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/quantity|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/quantity|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/quantity|unit", "m")
                .put("test_statuses/test_statuses:0/quantity|magnitude", "1")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode quantity2 = getDataValue(rawComposition2);

        assertThat(quantity2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(quantity2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void quantityInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/quantity|unit", "m")
                .put("test_statuses/test_statuses:0/quantity|magnitude", "1")
                .put("test_statuses/test_statuses:0/quantity|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/quantity|unit", "m")
                .put("test_statuses/test_statuses:0/quantity|magnitude", "1")
                .put("test_statuses/test_statuses:0/quantity|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void date() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/date", "2017-10-01")
                .put("test_statuses/test_statuses:0/date|normal_status", "L")
                .put("test_statuses/test_statuses:0/date|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();

        JsonNode date1 = getDataValue(rawComposition);

        assertThat(date1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(date1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/date|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/date|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/date", "2017-10-01")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode date2 = getDataValue(rawComposition2);

        assertThat(date2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(date2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void dateInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/date", "2017-10-01")
                .put("test_statuses/test_statuses:0/date|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/date", "2017-10-01")
                .put("test_statuses/test_statuses:0/quantity|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void time() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/time", "13:20")
                .put("test_statuses/test_statuses:0/time|normal_status", "L")
                .put("test_statuses/test_statuses:0/time|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();
        

        JsonNode time1 = getDataValue(rawComposition);

        assertThat(time1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(time1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/time|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/time|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/time", "13:20")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode time2 = getDataValue(rawComposition2);

        assertThat(time2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(time2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void timeInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/time", "13:20")
                .put("test_statuses/test_statuses:0/time|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/time", "13:20")
                .put("test_statuses/test_statuses:0/time|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void dateTime() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/datetime", "2017-10-01T13:20:00Z")
                .put("test_statuses/test_statuses:0/datetime|normal_status", "L")
                .put("test_statuses/test_statuses:0/datetime|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();
        

        JsonNode dateTime1 = getDataValue(rawComposition);

        assertThat(dateTime1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(dateTime1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/datetime|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/datetime|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/datetime", "2017-10-01T13:20:00Z")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();
        assertThat(rawComposition2).isNotNull();

        JsonNode dateTime2 = getDataValue(rawComposition2);

        assertThat(dateTime2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(dateTime2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void dateTimeInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/datetime", "2017-10-01T13:20:00Z")
                .put("test_statuses/test_statuses:0/datetime|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/datetime", "2017-10-01T13:20:00Z")
                .put("test_statuses/test_statuses:0/datetime|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void count() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/count", "17")
                .put("test_statuses/test_statuses:0/count|normal_status", "L")
                .put("test_statuses/test_statuses:0/count|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();
        

        JsonNode count1 = getDataValue(rawComposition);

        assertThat(count1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(count1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/count|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/count|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/count", "17")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();

        JsonNode count2 = getDataValue(rawComposition2);

        assertThat(count2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(count2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void countInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/count", "17")
                .put("test_statuses/test_statuses:0/count|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/count", "17")
                .put("test_statuses/test_statuses:0/count|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    @Test
    public void proportion() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/proportion|numerator", "17")
                .put("test_statuses/test_statuses:0/proportion|denominator", "33")
                .put("test_statuses/test_statuses:0/proportion|normal_status", "L")
                .put("test_statuses/test_statuses:0/proportion|magnitude_status", ">=")
                .build();

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition).isNotNull();
        

        JsonNode proportion1 = getDataValue(rawComposition);

        assertThat(proportion1.get("normal_status").get("code_string").asText()).isEqualTo("L");
        assertThat(proportion1.get("magnitude_status").asText()).isEqualTo(">=");

        Map<String, Object> returned1 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition.toString(),
                objectMapper
        );
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/proportion|normal_status", "L"));
        assertThat(returned1).contains(entry("test_statuses/test_statuses:0/proportion|magnitude_status", ">="));

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/proportion|numerator", "17")
                .put("test_statuses/test_statuses:0/proportion|denominator", "33")
                .build();

        JsonNode rawComposition2 = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper
        );
        assertThat(rawComposition2).isNotNull();
        assertThat(rawComposition2).isNotNull();

        JsonNode proportion2 = getDataValue(rawComposition2);

        assertThat(proportion2.get("normal_status")).isInstanceOf(NullNode.class);
        assertThat(proportion2.get("magnitude_status").asText()).isEqualTo("null");

        Map<String, Object> returned2 = getCompositionConverter().convertRawToFlat(
                template,
                "en",
                rawComposition2.toString(),
                objectMapper
        );
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|normal_status"))).isEmpty();
        assertThat(returned2.keySet().stream().filter(k -> k.contains("|magnitude_status"))).isEmpty();
    }

    @Test
    public void proportionInvalid() throws Exception {
        String template = getFileContent("/res/test_statuses.opt");

        Map<String, String> flatComposition = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/proportion|numerator", "17")
                .put("test_statuses/test_statuses:0/proportion|denominator", "33")
                .put("test_statuses/test_statuses:0/proportion|normal_status", "X")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid NORMAL_STATUS code: X");

        Map<String, String> flatComposition2 = ImmutableMap.<String, String>builder()
                .put("ctx/language", "sl")
                .put("ctx/territory", "SI")
                .put("ctx/id_scheme", "ispek")
                .put("ctx/id_namespace", "ispek")
                .put("ctx/composer_name", "George Orwell")
                .put("test_statuses/test_statuses:0/proportion|numerator", "17")
                .put("test_statuses/test_statuses:0/proportion|denominator", "33")
                .put("test_statuses/test_statuses:0/proportion|magnitude_status", "!")
                .build();

        assertThatThrownBy(() -> getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition2),
                Collections.emptyMap(),
                objectMapper))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Invalid MAGNITUDE_STATUS: !");
    }

    private JsonNode getDataValue(JsonNode composition) {
        return composition.get("content").get(0).get("data").get("events").get(0).get("data").get("items").get(0).get("value");
    }
}
