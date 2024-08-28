package com.allclear.tastytrack.domain.auth;

import com.allclear.tastytrack.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record UserDetailsImpl(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return null;
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {

        return false;
    }

    @Override
    public boolean isAccountNonLocked() {

        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return false;
    }

    @Override
    public boolean isEnabled() {

        return false;
    }

}
