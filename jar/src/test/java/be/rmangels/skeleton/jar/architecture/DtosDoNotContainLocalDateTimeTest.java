package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.infrastructure.api.Dto;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Dtos should not contain a LocaleDateTime. LocalDateTime does not contain a timezone.
 */
public class DtosDoNotContainLocalDateTimeTest extends AbstractStaticAnalysisTest {

    @Test
    public void dtosDoNotContainLocalDateTime() {
        Set<Field> fields = fields();
        fields.forEach(
                field -> {
                    if (!field.getType().isPrimitive() && LocalDateTime.class.isAssignableFrom(field.getType())) {
                        Assertions.fail(String.format("Class '%s' contains a LocalDateTime field '%s'. This is not allowed as this gives a bad JSON representation.",
                                field.getDeclaringClass().getName(),
                                field.getName()));
                    }
                }
        );
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Dto.class));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

}
