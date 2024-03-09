package com.civilwar.boardsignal.user.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
@Table(name = "USER_CATEGORY_TABLE")
public class UserCategory {

    private static final String USER_CATEGORY = "user_category";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_CATEGORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_CATEGORY_USER_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User user;

    @Column(name = "USER_CATEGORY_CATEGORY")
    @Enumerated(STRING)
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private UserCategory(
        @NonNull User user,
        @NonNull Category category
    ) {
        this.user = user;
        this.category = category;
    }

    public static UserCategory of(
        User user,
        Category category
    ) {
        return UserCategory.builder()
            .user(user)
            .category(category)
            .build();
    }
}
