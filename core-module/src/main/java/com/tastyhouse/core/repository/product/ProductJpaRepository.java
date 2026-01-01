package com.tastyhouse.core.repository.product;

import com.tastyhouse.core.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    List<Product> findByPlaceIdOrderByIsRepresentativeDescRatingDescIdAsc(Long placeId);

    List<Product> findByPlaceIdAndIsActiveTrueOrderBySortAsc(Long placeId);
}
