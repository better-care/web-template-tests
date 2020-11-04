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
import org.joda.time.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@ExtendWith(WebTemplateTestExtension.class)
public class DurationTest extends AbstractWebTemplateTest {

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
    public void defaultDuration() throws Exception {
        String template = getFileContent("/Testing.opt");
        String flatCompositionString = getFileContent("/DefaultDurationTest.json");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                flatCompositionString,
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);
        assertThat(flatComposition).isNotEmpty();
        assertThat(flatComposition).contains(entry("encounter/testing:0/duration", Period.months(2).toString()));
    }

    @Test
    public void constrainedDurations() throws Exception {
        String template = getFileContent("/br-moh1.xml");
        String flatCompositionString = getFileContent("/DurationsBrMoh.json");

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                flatCompositionString,
                ImmutableMap.of(CompositionBuilderContextKey.LANGUAGE.getKey(), "en"),
                objectMapper);

        Map<String, Object> flatComposition = getCompositionConverter().convertRawToFlat(template, "en", rawComposition.toString(), objectMapper);

        assertThat(flatComposition).isNotEmpty();

        Period duration1 = Period.parse((String)flatComposition.get(
                "ficha_individual_da_aten_cao_basica/resumo_do_atendimento/aleitamento_materno/infant_feeding_summary:0/total_duration_of_breast_feeding"));
        assertThat(duration1.getHours()).isZero();
        assertThat(duration1.getMinutes()).isZero();
        assertThat(duration1.getSeconds()).isZero();
        assertThat(duration1.getMillis()).isZero();

        Period duration2 = Period.parse((String)flatComposition.get(
                "ficha_individual_da_aten_cao_basica/resumo_do_atendimento/aleitamento_materno/infant_feeding_summary:0/age_commenced_solid_foods"));
        assertThat(duration2.getHours()).isZero();
        assertThat(duration2.getMinutes()).isZero();
        assertThat(duration2.getSeconds()).isZero();
        assertThat(duration2.getMillis()).isZero();

        Period duration3 = Period.parse((String)flatComposition.get(
                "ficha_individual_da_aten_cao_basica/resumo_do_atendimento/gestante/pregnancy_summary:0/current_pregnancy/labour_or_delivery:0/offspring:0/duration_of_second_stage"));
        assertThat(duration3.getYears()).isZero();
        assertThat(duration3.getMonths()).isZero();
        assertThat(duration3.getWeeks()).isZero();
        assertThat(duration3.getDays()).isZero();
        assertThat(duration3.getSeconds()).isZero();
        assertThat(duration3.getMillis()).isZero();
    }
}
