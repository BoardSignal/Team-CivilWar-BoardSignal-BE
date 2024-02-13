package com.civilwar.boardsignal.user.domain.entity;

import static com.civilwar.boardsignal.common.exception.CommonValidationError.getNotEmptyMessage;
import static com.civilwar.boardsignal.common.exception.CommonValidationError.getNotNullMessage;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.Role;
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

    private static final String USER = "user";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_EMAIL")
    private String email;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name = "USER_NICKNAME")
    private String nickname;

    @Column(name = "USER_PROVIDER")
    private String provider;

    @Column(name = "USER_PROVIDER_ID")
    private String providerId;

    @Column(name = "USER_ROLE")
    private Role role;

    @Column(name = "USER_PREFER_CATEGORY")
    private Category preferCategory;

    @Column(name = "USER_ADDRESS")
    private String address;

    @Column(name = "USER_PROFILE_IMAMGE_URL")
    private String profileImageUrl;

    @Column(name = "USER_BIRTH")
    private int birth;

    @Column(name = "USER_AGE_GROUP")
    private AgeGroup ageGroup;

    @Column(name = "USER_GENDER")
    private Gender gender;

    @Column(name = "USER_MANNER_SCORE")
    private double mannerScore;

    @Builder(access = AccessLevel.PRIVATE)
    private User(
        String email,
        String name,
        String nickname,
        String provider,
        String providerId,
        Role role,
        Category preferCategory,
        String address,
        String profileImageUrl,
        int birth,
        AgeGroup ageGroup,
        Gender gender
    ) {
        Assert.hasText(email, getNotEmptyMessage(USER, "email"));
        Assert.hasText(name, getNotEmptyMessage(USER, "name"));
        Assert.hasText(nickname, getNotEmptyMessage(USER, "nickname"));
        Assert.hasText(provider, getNotEmptyMessage(USER, "provider"));
        Assert.hasText(providerId, getNotEmptyMessage(USER, "providerId"));
        Assert.notNull(role, getNotNullMessage(USER, "role"));
        Assert.notNull(preferCategory, getNotNullMessage(USER, "preferCategory"));
        Assert.hasText(address, getNotEmptyMessage(USER, "address"));
        Assert.hasText(profileImageUrl, getNotEmptyMessage(USER, "profileImageUrl"));
        Assert.notNull(ageGroup, getNotNullMessage(USER, "ageGroup"));
        Assert.notNull(gender, getNotNullMessage(USER, "gender"));
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.preferCategory = preferCategory;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
        this.birth = birth;
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.mannerScore = 36.5;
    }

    public static User of(
        String email,
        String name,
        String nickname,
        String provider,
        String providerId,
        Role role,
        Category preferCategory,
        String address,
        String profileImageUrl,
        int birth,
        AgeGroup ageGroup,
        Gender gender
    ) {
        return User.builder()
            .email(email)
            .name(name)
            .nickname(nickname)
            .provider(provider)
            .providerId(providerId)
            .role(role)
            .preferCategory(preferCategory)
            .address(address)
            .profileImageUrl(profileImageUrl)
            .birth(birth)
            .ageGroup(ageGroup)
            .gender(gender)
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
