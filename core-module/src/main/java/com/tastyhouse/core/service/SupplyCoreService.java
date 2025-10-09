package com.tastyhouse.core.service;

import com.tastyhouse.core.entity.product.Supply;
import com.tastyhouse.core.repository.product.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplyCoreService {

    private final SupplyRepository supplyRepository;

    public List<Supply> findAllSupplies() {
        return supplyRepository.findAll();
    }
}
