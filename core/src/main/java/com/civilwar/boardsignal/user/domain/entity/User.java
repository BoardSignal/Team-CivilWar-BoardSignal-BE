package com.civilwar.boardsignal.user.domain.entity;

import static com.civilwar.boardsignal.common.exception.CommonValidationError.getNotEmptyMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "USER_TABLE")
public class User implements UserDetails {

    public static final String USER = "user";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name = "USER_PROVIDER")
    private String provider;

    @Column(name = "USER_PROVIDER_ID")
    private String providerId;

    @Column(name = "USER_ROLE")
    private Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private User(
        String name,
        String provider,
        String providerId
    ) {
        Assert.hasText(name, getNotEmptyMessage(USER, name));
        Assert.hasText(provider, getNotEmptyMessage(USER, provider));
        Assert.hasText(providerId, getNotEmptyMessage(USER, providerId));
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.USER;
    }

    public static User of(
        String name,
        String provider,
        String providerId
    ) {
        return User.builder()
            .name(name)
            .provider(provider)
            .providerId(providerId)
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return providerId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
