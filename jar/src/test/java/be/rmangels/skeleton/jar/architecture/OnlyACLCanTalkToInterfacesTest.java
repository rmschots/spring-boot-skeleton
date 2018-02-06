package be.rmangels.skeleton.jar.architecture;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

public class OnlyACLCanTalkToInterfacesTest extends AbstractStaticAnalysisTest {

    @Test
    public void onlyACLCanTalkToInterfacesTest() {
        fields()
                .forEach(field -> {
                            if (fieldIsOfInterfaceModule(field)) {
                                Assertions.fail(String.format("Class '%s' contains a field '%s' of the interfaces layer. This is not allowed: everything should pass the ACL layer",
                                        field.getDeclaringClass().getName(),
                                        field.getName()));
                            }
                        }
                );
    }

    private boolean fieldIsOfInterfaceModule(Field field) {
        return !field.getType().isPrimitive() && field.getType().getPackage().getName().contains(String.format("%s.interfaces", BASE_PACKAGE));
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*be\\.rmangels\\.skeleton\\.acl.*")));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*be\\.rmangels\\.skeleton\\.interfaces.*")));
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(BASE_PACKAGE);
        assertThat(candidateComponents).isNotEmpty();
        return candidateComponents;
    }


}
