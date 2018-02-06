package be.rmangels.skeleton.jar.architecture;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;

public class ComponentInterfaceMethodsAreReadOperationsTest extends AbstractStaticAnalysisTest {

    @Test
    public void onlyGettersOnComponentInterfaces() {
        publicMethods().forEach(
                method -> {
                    System.out.println(method);
                    if (!(method.getName().startsWith("get") || method.getName().startsWith("is") || method.getName().startsWith("find"))) {
                        Assertions.fail(String.format("Class '%s' contains a non-getter method '%s'. This is not allowed: the component-interfaces are only allowed to read information of the component, but not change it.",
                                method.getDeclaringClass().getName(),
                                method.getName()));
                    }
                }
        );
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new InterfacesComponentProvider();
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*be\\.rmangels\\.skeleton.*\\.iface.*")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

}
