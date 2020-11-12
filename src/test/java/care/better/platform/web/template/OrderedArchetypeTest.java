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

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Marko Narat
 */
@ExtendWith(WebTemplateTestExtension.class)
public class OrderedArchetypeTest extends AbstractWebTemplateTest {

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
    public void orderedFailed() throws Exception {
        String template = getFileContent("/res/IDCR - Cancer MDT Output Report.v0 ordered.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/OrderedFailed.json"), new TypeReference<Map<String, Object>>() {
        });

        assertThatThrownBy(() -> {
            getCompositionConverter().convertFlatToRaw(
                    template,
                    "en",
                    objectMapper.writeValueAsString(flatComposition),
                    ImmutableMap.of(
                            CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                            CompositionBuilderContextKey.TERRITORY.getKey(), "IE",
                            CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                    objectMapper);
        }).isInstanceOf(Exception.class);

    }

    @Test
    public void orderedOk() throws Exception {
        String template = getFileContent("/res/IDCR - Cancer MDT Output Report.v0 ordered.xml");
        Map<String, Object> flatComposition = objectMapper.readValue(getFileContent("/res/OrderedOk.json"), new TypeReference<Map<String, Object>>() {
        });

        JsonNode rawComposition = getCompositionConverter().convertFlatToRaw(
                template,
                "en",
                objectMapper.writeValueAsString(flatComposition),
                ImmutableMap.of(
                        CompositionBuilderContextKey.LANGUAGE.getKey(), "en",
                        CompositionBuilderContextKey.TERRITORY.getKey(), "IE",
                        CompositionBuilderContextKey.COMPOSER_NAME.getKey(), "composer"),
                objectMapper);

        assertThat(getCompositionValidator().validate(template, rawComposition.toString())).isEmpty();

    }

}
