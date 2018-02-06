package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.config.ApplicationProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyUsageTest extends AbstractStaticAnalysisTest {

    private static Set<Class<?>> VALUE_ANNOTATION_EXCLUSIONS = newHashSet(ApplicationProperties.class);

    @Test
    public void checkValueAnnotations() {
        List<Class<?>> classesWithValueAnnotations = filteredClasses()
                .filter(aClass -> stream(aClass.getDeclaredFields()).anyMatch(field -> field.isAnnotationPresent(Value.class)))
                .collect(toList());

        assertThat(classesWithValueAnnotations)
                .overridingErrorMessage("The following classes have value annotations while they shouldn't: " +
                        classesWithValueAnnotations.stream().map(Class::getCanonicalName).collect(joining(",")))
                .isEmpty();
    }

    @Test
    public void checkEnvironmentInjections() {
        List<Class<?>> classesWithEnvironmentInjection = filteredClasses()
                .filter(aClass -> stream(aClass.getDeclaredFields()).anyMatch(this::isInjectedEnvironmentField))
                .collect(toList());

        assertThat(classesWithEnvironmentInjection)
                .overridingErrorMessage("The following classes have environment injections while they shouldn't: " +
                        classesWithEnvironmentInjection.stream().map(Class::getCanonicalName).collect(joining(",")))
                .isEmpty();
    }

    private boolean isInjectedEnvironmentField(Field field) {
        return (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(Autowired.class))
                && field.getType().equals(Environment.class);
    }

    private Stream<? extends Class<?>> filteredClasses() {
        return classes().filter(aClass -> !VALUE_ANNOTATION_EXCLUSIONS.contains(aClass));
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

}
