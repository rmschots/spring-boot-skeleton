package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.infrastructure.ddd.AggregateRoot;
import be.rmangels.skeleton.infrastructure.ddd.ValueObject;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static java.lang.String.format;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static java.util.stream.Collectors.toList;
import static javax.persistence.AccessType.FIELD;
import static org.assertj.core.api.Assertions.assertThat;

public class AggregatesAndEntitiesHaveExplicitColumnNamesTest extends AbstractStaticAnalysisTest {

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(AggregateRoot.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(ValueObject.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Embeddable.class));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

    @Test
    public void aggregatesAndEntitiesHaveExplicitColumnNames() {
        List<Class<?>> invalidClassesGeenExplicieteKolomnamen = classes()
                .filter(beanClass -> isEmbeddableEnValueObjectOrNeither(beanClass) && !requiresExplicitColumnNameAnnotation(beanClass))
                .collect(toList());
        assertThat(invalidClassesGeenExplicieteKolomnamen)
                .withFailMessage(format("The following classes should have an explicit columnname declaration:\r\n\t%s", classesInErrorToString(invalidClassesGeenExplicieteKolomnamen)))
                .isEmpty();
    }

    private boolean isEmbeddableEnValueObjectOrNeither(Class<?> classss) {
        return (classss.isAnnotationPresent(Embeddable.class) && ValueObject.class.isAssignableFrom(classss)) ||
                (!classss.isAnnotationPresent(Embeddable.class) && !ValueObject.class.isAssignableFrom(classss));
    }

    private boolean requiresExplicitColumnNameAnnotation(Class<?> classss) {
        return Arrays.stream(classss.getDeclaredFields())
                .filter(field -> !isTransient(field.getModifiers()) &&
                        !isStatic(field.getModifiers()) &&
                        !field.isAnnotationPresent(Embedded.class) &&
                        !field.isAnnotationPresent(CollectionTable.class)
                )
                .allMatch(nonStaticField -> nonStaticField.isAnnotationPresent(Column.class));
    }

    private boolean isFieldAccessType(Class<?> classss) {
        return classss.isAnnotationPresent(Embeddable.class) &&
                classss.isAnnotationPresent(Access.class) &&
                classss.getAnnotation(Access.class).value().equals(FIELD);
    }

    private String classesInErrorToString(List<Class<?>> classesInError) {
        return classesInError.stream().map(Class::getName).collect(Collectors.joining("\r\n\t"));
    }
}
