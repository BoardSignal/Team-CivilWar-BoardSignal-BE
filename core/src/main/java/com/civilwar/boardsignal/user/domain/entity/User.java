package com.civilwar.boardsignal.user.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "USER_TABLE")
public class User implements UserDetails {

    private static final String USER = "user";

    @OneToMany(mappedBy = "user", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<UserCategory> userCategories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<UserFcmToken> userFcmTokens = new ArrayList<>();

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

    @Column(name = "USER_LINE")
    private String line;

    @Column(name = "USER_STATION")
    private String station;

    @Column(name = "USER_PROFILE_IMAMGE_URL")
    private String profileImageUrl;

    @Column(name = "USER_BIRTH")
    private int birth;

    @Column(name = "USER_AGE_GROUP")
    @Enumerated(STRING)
    private AgeGroup ageGroup;

    @Column(name = "USER_GENDER")
    @Enumerated(STRING)
    private Gender gender;

    @Column(name = "USER_MANNER_SCORE")
    private double mannerScore;

    @Column(name = "USER_IS_JOINED")
    private Boolean isJoined;

    @Column(name = "USER_SIGNAL")
    private int signal;

    @Builder(access = AccessLevel.PRIVATE)
    private User(
        @NonNull String email,
        @NonNull String name,
        @NonNull String nickname,
        @NonNull String provider,
        @NonNull String providerId,
        @NonNull String profileImageUrl,
        @NonNull int birth,
        @NonNull AgeGroup ageGroup,
        @NonNull Gender gender
    ) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.role = Role.USER;
        this.profileImageUrl = profileImageUrl;
        this.birth = birth;
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.mannerScore = 36.5;
        this.isJoined = Boolean.FALSE;
        this.signal = 0;
    }

    public static User of(
        String email,
        String name,
        String nickname,
        String provider,
        String providerId,
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
            .profileImageUrl(profileImageUrl)
            .birth(birth)
            .ageGroup(ageGroup)
            .gender(gender)
            .build();
    }

    public void updateUser(
        String nickname,
        List<Category> categories,
        String line,
        String station,
        String profileImageUrl
    ) {
        this.nickname = nickname;
        this.line = line;
        this.station = station;
        this.userCategories.clear();
        categories.stream()
            .map(category -> UserCategory.of(this, category))
            .forEach(this.userCategories::add);
        this.profileImageUrl = profileImageUrl;

        //필요한 정보를 모두 등록했으므로, 회원가입 여부 참으로 변경
        this.isJoined = true;
    }

    public void updateMannerScore(double score) {
        this.mannerScore += score;
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
