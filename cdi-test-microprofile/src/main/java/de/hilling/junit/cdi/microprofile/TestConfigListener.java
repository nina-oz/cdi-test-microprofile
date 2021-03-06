package de.hilling.junit.cdi.microprofile;

import de.hilling.junit.cdi.lifecycle.TestEvent;
import de.hilling.junit.cdi.scope.EventType;
import de.hilling.junit.cdi.scope.TestSuiteScoped;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Arrays;

@TestSuiteScoped
public class TestConfigListener {

    private ExtensionContext startingEvent;
    private ExtensionContext finishingEvent;

    @Inject
    private TestPropertiesHolder testProperties;

    protected void observeStarting(@Observes @TestEvent(EventType.STARTING) ExtensionContext testEvent) {
        testEvent.getTestClass()
                 .ifPresent(testClass -> Arrays.stream(testClass.getAnnotationsByType(ConfigPropertyValue.class))
                                               .forEach(this::applyPropertyValue));
        testEvent.getTestMethod()
                 .ifPresent(testMethod -> Arrays.stream(testMethod.getAnnotationsByType(ConfigPropertyValue.class))
                                                .forEach(this::applyPropertyValue));
        startingEvent = testEvent;
    }

    private void applyPropertyValue(ConfigPropertyValue configPropertyValue) {
        testProperties.put(configPropertyValue.name(), configPropertyValue.value());
    }

    protected void observeFinishing(@Observes @TestEvent(EventType.FINISHING) ExtensionContext testEvent) {
        finishingEvent = testEvent;
    }

    protected void observeFinished(@Observes @TestEvent(EventType.FINISHED) ExtensionContext testEvent) {
        finishingEvent = null;
        startingEvent = null;
    }

}
