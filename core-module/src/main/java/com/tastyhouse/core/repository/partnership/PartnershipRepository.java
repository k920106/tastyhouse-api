package com.tastyhouse.core.repository.partnership;

import com.tastyhouse.core.entity.partnership.PartnershipRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartnershipRepository {

    List<PartnershipRequest> findAllOrderByCreatedAtDesc();

    Page<PartnershipRequest> findAllOrderByCreatedAtDesc(Pageable pageable);
}
