package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.Brand;

import java.util.List;

public interface BrandRepository {
    List<Brand> findAll();
}
