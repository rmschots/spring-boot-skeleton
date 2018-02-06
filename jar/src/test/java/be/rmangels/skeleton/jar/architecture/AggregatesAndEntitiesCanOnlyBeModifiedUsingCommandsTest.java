package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.infrastructure.ddd.AggregateRoot;
import be.rmangels.skeleton.infrastructure.ddd.BaseEntity;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static java.lang.reflect.Modifier.isPrivate;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.fail;

public class AggregatesAndEntitiesCanOnlyBeModifiedUsingCommandsTest extends AbstractStaticAnalysisTest {

    @Test
    public void aggregatesAndEntitiesCanOnlyBeModifiedUsingCommands() {
        List<Method> violatingMethods = nonStaticMethods().stream()
                .filter(method -> !isPrivate(method.getModifiers()))
                .filter(method -> !method.getName().startsWith("has"))
                .filter(method -> !method.getName().startsWith("get"))
                .filter(method -> !method.getName().startsWith("is"))
                .filter(method -> !method.getName().startsWith("validate"))
                .filter(method -> !method.getName().startsWith("access")) // for methods in inner classes
                .filter(method -> !method.getName().startsWith("toString"))
                .collect(toList());
        if (violatingMethods.size() > 0) {
            violatingMethods
                    .forEach(method -> System.out.printf("==== Aggregates and base entities can only be modified using Aggragete::execute, remove %s#%s%n", method.getDeclaringClass().getName(), method.getName()));
            fail("Aggregates and base entities can only be modified using Aggragete::execute");
        }
    }


    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(BaseEntity.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(AggregateRoot.class));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

}
