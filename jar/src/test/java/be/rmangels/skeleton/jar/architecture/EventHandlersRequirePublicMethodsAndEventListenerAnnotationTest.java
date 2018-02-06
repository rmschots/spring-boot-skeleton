package be.rmangels.skeleton.jar.architecture;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.EventListener;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.reflect.Modifier.isPublic;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.fail;

public class EventHandlersRequirePublicMethodsAndEventListenerAnnotationTest extends AbstractStaticAnalysisTest {

    private static final Pattern EVENTHANDLER_PATTERN = Pattern.compile(".*EventHandler$");

    @Test
    public void eventHandlersRequirePublicMethodsAndEventListenerAnnotation() {
        eventHandlerPublicMethods()
                .forEach(this::validateEventHandlerPubliekeMethodeHeeftEventListenerAnnotatie);
    }

    private Set<Method> eventHandlerPublicMethods() {
        return classes()
                .flatMap(eventHandlerClass -> newHashSet(eventHandlerClass.getDeclaredMethods()).stream())
                .filter(method -> isPublic(method.getModifiers()))
                .collect(toSet());
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(EVENTHANDLER_PATTERN));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

    private void validateEventHandlerPubliekeMethodeHeeftEventListenerAnnotatie(Method eventHandlerPublicMethod) {
        System.out.printf("==== Checking event handler method %s#%s%n", eventHandlerPublicMethod.getDeclaringClass().getName(), eventHandlerPublicMethod.getName());
        if (!eventHandlerPublicMethod.isAnnotationPresent(EventListener.class)) {
            fail(String.format("'%s' is a public event handler method and requires @EventListener", eventHandlerPublicMethod.getName()));
        }
    }
}
