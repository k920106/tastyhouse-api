package com.tastyhouse.core.entity.policy;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "POLICY_DOCUMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PolicyDocument extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private PolicyType type;

    @Column(name = "version", nullable = false, length = 20)
    private String version;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "is_current", nullable = false)
    private Boolean current = false;

    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = true;

    @Column(name = "effective_date", nullable = false)
    private LocalDateTime effectiveDate;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Builder
    public PolicyDocument(PolicyType type, String version, String title, String content,
                         Boolean current, Boolean mandatory, LocalDateTime effectiveDate,
                         String createdBy, String updatedBy) {
        this.type = type;
        this.version = version;
        this.title = title;
        this.content = content;
        this.current = current;
        this.mandatory = mandatory;
        this.effectiveDate = effectiveDate;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public void updateCurrent(Boolean current) {
        this.current = current;
    }

    public void update(String title, String content, Boolean mandatory,
                      LocalDateTime effectiveDate, String updatedBy) {
        this.title = title;
        this.content = content;
        this.mandatory = mandatory;
        this.effectiveDate = effectiveDate;
        this.updatedBy = updatedBy;
    }
}
