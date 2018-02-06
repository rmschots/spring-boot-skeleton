package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.infrastructure.test.UnitTest;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.toSet;

abstract class AbstractStaticAnalysisTest extends UnitTest {

    abstract protected Set<BeanDefinition> beanDefinitions();

    protected Stream<Class<?>> classes() {
        return beanDefinitions().stream()
                .map(this::toClass);
    }

    protected Set<Field> fields() {
        return classes()
                .flatMap(resourceClass -> Stream.of(resourceClass.getDeclaredFields()))
                .collect(toSet());
    }

    protected Set<Method> nonStaticMethods() {
        return classes()
                .flatMap(resourceClass -> Stream.of(resourceClass.getDeclaredMethods()))
                .filter(method -> !isStatic(method.getModifiers()))
                .collect(toSet());
    }

    protected Set<Method> publicMethods() {
        return classes()
                .flatMap(resourceClass -> Stream.of(resourceClass.getDeclaredMethods()))
                .filter(method -> isPublic(method.getModifiers()))
                .collect(toSet());
    }

    protected Class<?> toClass(BeanDefinition beanDefinition) {
        try {
            return Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected static class InterfacesComponentProvider extends ClassPathScanningCandidateComponentProvider {

        public InterfacesComponentProvider() {
            super(false);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return beanDefinition.getMetadata().isInterface();
        }
    }

}
