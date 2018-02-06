package be.rmangels.skeleton.jar.architecture;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;

/**
 * Optionals niet gebruiken op fields, maar wel in de API van een klasse (bv de getters)
 */
public class OptionalsNotUsedInFieldsTest extends AbstractStaticAnalysisTest {

    @Test
    public void optionalsNotUsedInFieldsTest() {
        fields().forEach(
                field -> {
                    if (!field.getType().isPrimitive() && Optional.class.isAssignableFrom(field.getType())) {
                        Assertions.fail(String.format("Class '%s' contains optional field '%s'. This is not allowed: fields cannot be optional, only methods/getter van use optionals.",
                                field.getDeclaringClass().getName(),
                                field.getName()));
                    }
                }
        );
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }


}
