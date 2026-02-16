package com.tastyhouse.core.entity.user;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; // Add Setter annotation

@Getter
@Setter // Add Setter annotation for all fields, or selectively if preferred
@Entity
@Table(name = "MEMBER")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "birth_date")
    private Integer birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10, columnDefinition = "VARCHAR(10)")
    private Gender gender;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_grade", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MemberGrade memberGrade = MemberGrade.NEWCOMER;

    @Column(name = "profile_image_file_id")
    private Long profileImageFileId;

    @Column(name = "status_message", length = 200)
    private String statusMessage;

    @Column(name = "push_notification_enabled", nullable = false)
    private Boolean pushNotificationEnabled = true;

    @Column(name = "marketing_info_enabled", nullable = false)
    private Boolean marketingInfoEnabled = false;

    @Column(name = "event_info_enabled", nullable = false)
    private Boolean eventInfoEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    // No-argument constructor required by JPA
    public Member() {
    }

    // Convenient constructor for test user creation
    public Member(String username, String password, String nickname, String fullName, Gender gender) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.fullName = fullName;
        this.gender = gender;
        this.memberGrade = MemberGrade.NEWCOMER; // Default
        this.memberStatus = MemberStatus.ACTIVE; // Default
        this.pushNotificationEnabled = true; // Default
        this.marketingInfoEnabled = false; // Default
        this.eventInfoEnabled = false; // Default
    }
}

