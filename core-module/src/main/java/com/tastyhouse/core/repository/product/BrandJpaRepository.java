package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandJpaRepository extends JpaRepository<Brand, Long> {
}
