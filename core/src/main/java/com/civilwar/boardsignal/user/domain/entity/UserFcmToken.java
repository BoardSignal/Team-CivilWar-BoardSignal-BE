package com.civilwar.boardsignal.user.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import com.civilwar.boardsignal.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "USER_FCM_TOKEN_TABLE")
public class UserFcmToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_FCM_TOKEN_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_FCM_TOKEN_USER_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User user;

    @Column(name = "USER_FCM_TOKEN_TOKEN")
    private String token;

    @Column(name = "USER_FCM_TOKEN_IS_VALID")
    private boolean isValid;

    @Builder(access = AccessLevel.PRIVATE)
    private UserFcmToken(
        @NonNull User user,
        @NonNull String token
    ) {
        this.user = user;
        user.getUserFcmTokens().add(this);
        this.token = token;
        this.isValid = true;
    }

    public static UserFcmToken of(
        User user,
        String token
    ) {
        return UserFcmToken.builder()
            .user(user)
            .token(token)
            .build();
    }
}
