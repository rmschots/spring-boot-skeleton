package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.infrastructure.ddd.AggregateRoot;
import be.rmangels.skeleton.infrastructure.ddd.ValueObject;
import be.rmangels.skeleton.infrastructure.messaging.Command;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class ImmutableGettersTest extends AbstractStaticAnalysisTest {

    @Test
    public void aggegateRootsAndValueObjectsRequirePrivateNoArgsConstructor() {
        List<Class<?>> invalidClasses = classes()
                .filter(beanClass -> {
                    try {
                        beanClass.getDeclaredConstructor();
                        return false;
                    } catch (NoSuchMethodException e) {
                        return true;
                    }
                }).collect(toList());
        assertThat(invalidClasses).withFailMessage(classesInErrorToString(invalidClasses)).isEmpty();
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(AggregateRoot.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(ValueObject.class));
        scanner.addExcludeFilter(new AssignableTypeFilter(Command.class));
        scanner.addExcludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Test.*")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

    private String classesInErrorToString(List<Class<?>> classesInError) {
        return classesInError.stream().map(Class::getName).collect(Collectors.joining("\r\n "));
    }

    @Test
    public void returnedListsFromAggregateRootsAndValueObjectsNeedToBeImmutable() {
        List<Method> mutableMethods = nonStaticMethods().stream()
                .filter(method -> method.getName().startsWith("get"))
                .map(method -> {
                    try {
                        Class<?> classsss = method.getDeclaringClass();
                        if (Collection.class.isAssignableFrom(method.getReturnType()) && method.getParameterCount() == 0) {
                            Constructor<?> constructor = classsss.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            Object objectInstance = constructor.newInstance();
                            Object result = method.invoke(objectInstance);
                            System.out.println(method);
                            if (result.getClass().getEnclosingClass() == null) {
                                return method;
                            }
                        }
                    } catch (Exception ignored) {
                        if (!(ignored.getCause() instanceof NullPointerException)) {
                            ignored.printStackTrace();
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(toList());
        assertThat(mutableMethods)
                .withFailMessage(format("The following methods should return an immutable collection:\r\n\t%s", methodsToString(mutableMethods)))
                .isEmpty();
    }

    private String methodsToString(List<Method> mutableMethods) {
        return mutableMethods.stream()
                .map(mutableMethod -> mutableMethod.getDeclaringClass().getName() + "::" + mutableMethod.getName())
                .collect(Collectors.joining("\r\n\t"));
    }
}
