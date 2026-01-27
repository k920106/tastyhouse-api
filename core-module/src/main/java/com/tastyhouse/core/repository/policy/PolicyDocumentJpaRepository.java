package com.tastyhouse.core.repository.policy;

import com.tastyhouse.core.entity.policy.PolicyDocument;
import com.tastyhouse.core.entity.policy.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyDocumentJpaRepository extends JpaRepository<PolicyDocument, Long> {

    Optional<PolicyDocument> findByTypeAndCurrent(PolicyType type, Boolean current);

    Optional<PolicyDocument> findByTypeAndVersion(PolicyType type, String version);
}
