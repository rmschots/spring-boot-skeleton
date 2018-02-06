package be.rmangels.skeleton.common.domain.user;

import be.rmangels.skeleton.iface.user.UserId;
import be.rmangels.skeleton.infrastructure.ddd.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toSet;

@Builder
@Getter
@AllArgsConstructor
public final class User extends ValueObject implements UserDetails {

    @NotNull
    private UserId userId;
    @NotNull
    private String username;
    @NotNull
    private Set<Feature> enabledFeatures;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private User() {
    }


    public Set<Feature> getEnabledFeatures() {
        return unmodifiableSet(enabledFeatures);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return unmodifiableSet(enabledFeatures.stream()
                .map(feature -> new SimpleGrantedAuthority("FEATURE_" + feature.name()))
                .collect(toSet()));
    }

    @Override
    public String getPassword() {
        return "NA";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
}
