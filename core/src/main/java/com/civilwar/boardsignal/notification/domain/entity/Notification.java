package com.civilwar.boardsignal.notification.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import com.civilwar.boardsignal.user.domain.entity.User;
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
@Table(name = "NOTIFICATION_TABLE")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTIFICATION_USER_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User user;

    @Column(name = "NOTIFICATION_IMAGE_URL")
    private String imageUrl;

    @Column(name = "NOTIFICATION_TITLE")
    private String title;

    @Column(name = "NOTIFICATION_BODY")
    private String body;

    @Column(name = "NOTIFICATION_ROOM_ID")
    private Long roomID;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(
        @NonNull User user,
        String imageUrl,
        @NonNull String title,
        @NonNull String body,
        Long roomID
    ) {
        this.user = user;
        this.imageUrl = imageUrl;
        this.title = title;
        this.body = body;
        this.roomID = roomID;
    }

    public static Notification of(
        User user,
        String imageUrl,
        String title,
        String body,
        Long roomId
    ) {
        return Notification.builder()
            .user(user)
            .imageUrl(imageUrl)
            .title(title)
            .body(body)
            .roomID(roomId)
            .build();
    }
}
