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

package care.better.platform.web.template.extension;

import care.better.platform.web.template.AbstractWebTemplateTest;
import care.better.platform.web.template.converter.CompositionConverter;
import care.better.platform.web.template.provider.WebTemplateProvider;
import care.better.platform.web.template.validator.CompositionValidator;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Consumer;

/**
 * @author Primoz Delopst
 */
public class WebTemplateTestExtension implements InvocationInterceptor {

    @Override
    public void interceptBeforeEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {

        AbstractWebTemplateTest abstractWebTemplateTest = extensionContext.getTestInstance()
                .filter(instance -> instance instanceof AbstractWebTemplateTest)
                .map(instance -> (AbstractWebTemplateTest)instance)
                .orElseThrow(() -> new IllegalStateException("Test class must be an instance of an AbstractWebTemplateTest and must be initialized!"));

        invokeSetter(abstractWebTemplateTest::setCompositionConverter, CompositionConverter.class);
        invokeSetter(abstractWebTemplateTest::setWebTemplateProvider, WebTemplateProvider.class);
        invokeSetter(abstractWebTemplateTest::setCompositionValidator, CompositionValidator.class);

        invocation.proceed();
    }

    @Override
    public void interceptAfterEachMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        AbstractWebTemplateTest abstractWebTemplateTest = extensionContext.getTestInstance()
                .filter(instance -> instance instanceof AbstractWebTemplateTest)
                .map(instance -> (AbstractWebTemplateTest)instance)
                .orElseThrow(() -> new IllegalStateException("Test class must an be instance of an AbstractWebTemplateTest and must be initialized!"));

        abstractWebTemplateTest.setCompositionConverter(null);
        abstractWebTemplateTest.setWebTemplateProvider(null);
        abstractWebTemplateTest.setCompositionValidator(null);

        invocation.proceed();
    }

    public <T> void invokeSetter(Consumer<T> consumer, Class<T> interfaceClass) {
        Iterator<T> iterator = ServiceLoader.load(interfaceClass).iterator();
        if (iterator.hasNext()) {
            consumer.accept(iterator.next());
        } else {
            throw new IllegalStateException("No implementations of " + interfaceClass.getSimpleName() + " were found!");
        }
    }
}
