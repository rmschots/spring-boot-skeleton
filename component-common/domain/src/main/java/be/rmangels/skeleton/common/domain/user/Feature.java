package be.rmangels.skeleton.common.domain.user;

import be.rmangels.skeleton.infrastructure.specification.Specification;

import java.util.Set;
import java.util.stream.Stream;

import static be.rmangels.skeleton.common.domain.user.Feature.HasRole.hasRole;
import static be.rmangels.skeleton.common.domain.user.Role.ADMIN;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;

public enum Feature {

    SHOW_ADMIN_MENU(
            hasRole(ADMIN)
    );

    public static Set<Feature> getAllowedFeatures(Role role) {
        return Stream.of(Feature.values())
                .filter(feature -> feature.isAllowedFor(newHashSet(role)))
                .collect(toSet());
    }

    static class HasRole implements Specification<Set<Role>> {

        static HasRole hasRole(Role role) {
            return new HasRole(role);
        }

        private final Role role;

        private HasRole(Role role) {
            this.role = role;
        }

        @Override
        public boolean isSatisfiedBy(Set<Role> roles) {
            return roles.contains(role);
        }
    }

    static class AllowedForEveryone implements Specification<Set<Role>> {

        static AllowedForEveryone allowedForEveryone() {
            return new AllowedForEveryone();
        }

        private AllowedForEveryone() {
        }

        @Override
        public boolean isSatisfiedBy(Set<Role> roles) {
            return true;
        }
    }

    private final Specification<Set<Role>> isAllowed;

    Feature(Specification<Set<Role>> isAllowed) {
        this.isAllowed = isAllowed;
    }

    public boolean isAllowedFor(Set<Role> roles) {
        return isAllowed.isSatisfiedBy(roles);
    }
}
