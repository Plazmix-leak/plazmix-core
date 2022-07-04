package net.plazmix.core.api.utility.test.impl;

import lombok.NonNull;
import net.plazmix.core.api.utility.query.AsyncUtil;
import net.plazmix.core.api.utility.test.AsyncTest;
import net.plazmix.core.api.utility.test.TestingFactory;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public class AsynchronousTestingFactory extends TestingFactory<AsyncTest> {

    public AsynchronousTestingFactory() {
        super(AsyncTest.class);
    }

    @Override
    public void executeTests(@NonNull Object objectWithTestMethods, Consumer<Method> testMethodConsumer) {
        Method[] testMethodArray = objectWithTestMethods.getClass().getMethods();

        for (Method testMethod : testMethodArray) {
            if (!testMethodCollection.contains(testMethod)) {
                continue;
            }

            AsyncUtil.submitThrowsAsync(() -> {

                if (testMethodConsumer != null) {
                    testMethodConsumer.accept(testMethod);
                }

                testMethod.invoke(objectWithTestMethods, new Object[testMethod.getParameterCount()]);
            });
        }
    }

}
