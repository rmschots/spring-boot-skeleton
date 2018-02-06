package be.rmangels.skeleton.jar.architecture;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.fail;

public class BuilderFactoryMethodsNameTest extends AbstractStaticAnalysisTest {

    private boolean isGeneratedMethod(Method method) {
        return method.getName().contains("$");
    }

    @Test
    public void builderFactoryMethodsDontStartWithAOrSome() {
        builderFactoryMethods()
                .forEach(this::validateBuilderFactoryMethodsDontStartWithEen);
    }

    private Set<Method> builderFactoryMethods() {
        return classes()
                .filter(builderClass -> isPublic(builderClass.getModifiers()))
                .flatMap(publicBuilderClass -> newHashSet(publicBuilderClass.getDeclaredMethods()).stream())
                .filter(method -> isStatic(method.getModifiers()))
                .filter(method -> !isGeneratedMethod(method))
                .collect(toSet());
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Builder")));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*TestBuilder")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

    private void validateBuilderFactoryMethodsDontStartWithEen(Method builderFactoryMethod) {
        System.out.printf("==== Checking builder factory method %s#%s%n", builderFactoryMethod.getDeclaringClass().getName(), builderFactoryMethod.getName());
        if (builderFactoryMethod.getName().matches("^(a[A-Z]).*") || builderFactoryMethod.getName().startsWith("some")) {
            fail(String.format("'%s' is a builder factory method and can't start with 'a' or 'some'", builderFactoryMethod.getName()));
        }
    }

}
