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

import care.better.platform.web.template.converter.CompositionConverter;
import care.better.platform.web.template.provider.WebTemplateProvider;
import care.better.platform.web.template.validator.CompositionValidator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Primoz Delopst
 */

public abstract class AbstractWebTemplateTest {
    ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("NoopMethodInAbstractClass")
    @BeforeEach
    public void setUp() {

    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @AfterEach
    public void tearDown() {

    }

    private CompositionConverter compositionConverter;
    private WebTemplateProvider webTemplateProvider;
    private CompositionValidator compositionValidator;

    public CompositionConverter getCompositionConverter() {
        return compositionConverter;
    }

    public void setCompositionConverter(CompositionConverter compositionConverter) {
        this.compositionConverter = compositionConverter;
    }

    public WebTemplateProvider getWebTemplateProvider() {
        return webTemplateProvider;
    }

    public void setWebTemplateProvider(WebTemplateProvider webTemplateProvider) {
        this.webTemplateProvider = webTemplateProvider;
    }

    public CompositionValidator getCompositionValidator() {
        return compositionValidator;
    }

    public void setCompositionValidator(CompositionValidator compositionValidator) {
        this.compositionValidator = compositionValidator;
    }

    protected String getFileContent(String fileName) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException(String.format("File with name {} was not found.", fileName));
            }
            String fileString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            if (fileName.endsWith(".json")) {
                mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
                fileString = mapper.writeValueAsString(mapper.readTree(fileString));
            }
            return fileString;
        }
    }
}
