package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.Supply;

import java.util.List;

public interface SupplyRepository {
    List<Supply> findAll();
}
