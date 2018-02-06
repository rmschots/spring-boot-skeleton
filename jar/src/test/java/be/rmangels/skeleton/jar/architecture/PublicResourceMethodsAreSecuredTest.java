package be.rmangels.skeleton.jar.architecture;

import be.rmangels.skeleton.common.domain.user.Feature;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

import static be.rmangels.skeleton.infrastructure.spring.InfrastructureConfig.BASE_PACKAGE;
import static java.lang.String.format;
import static org.junit.Assert.fail;

public class PublicResourceMethodsAreSecuredTest extends AbstractStaticAnalysisTest {

    private static final Pattern HAS_AUTORITY_FEATURE_PATTERN = Pattern.compile(format("^hasAuthority\\('FEATURE_(%s)'\\)$", StringUtils.join(Feature.values(), "|")));
    private static final Pattern HAS_ANY_AUTORITY_FEATURE_PATTERN = Pattern.compile(format("^hasAnyAuthority\\('FEATURE_(%s)'(, 'FEATURE_(%s)')*\\)$", StringUtils.join(Feature.values(), "|"), StringUtils.join(Feature.values(), "|")));

    @Test
    public void publicResourceMethodsAreSecured() {
        publicMethods().forEach(this::validatePublicResourceMethodIsSecured);
    }

    private void validatePublicResourceMethodIsSecured(Method publicResourceMethod) {
        System.out.printf("==== Checking public resource method %s#%s%n", publicResourceMethod.getDeclaringClass().getName(), publicResourceMethod.getName());
        if (!publicResourceMethod.isAnnotationPresent(PreAuthorize.class)) {
            fail(format("%s::%s is a public resource method and has to be secured with @PreAuthorize(\"hasAuthority('FEATURE_...')\") or @PreAuthorize(\"hasAnyAuthority('FEATURE_...', 'FEATURE_...', ...)\")", publicResourceMethod.getDeclaringClass().getName(), publicResourceMethod.getName()));
        }
        if (!HAS_AUTORITY_FEATURE_PATTERN.matcher(publicResourceMethod.getAnnotation(PreAuthorize.class).value()).matches() &&
                !HAS_ANY_AUTORITY_FEATURE_PATTERN.matcher(publicResourceMethod.getAnnotation(PreAuthorize.class).value()).matches()) {
            fail(format("%s::%s is a public resource method and has to be secured with @PreAuthorize(\"hasAuthority('FEATURE_...')\") or @PreAuthorize(\"hasAnyAuthority('FEATURE_...', 'FEATURE_...', ...)\")", publicResourceMethod.getDeclaringClass().getName(), publicResourceMethod.getName()));
        }
    }

    @Override
    protected Set<BeanDefinition> beanDefinitions() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*Resource")));
        return scanner.findCandidateComponents(BASE_PACKAGE);
    }

}
