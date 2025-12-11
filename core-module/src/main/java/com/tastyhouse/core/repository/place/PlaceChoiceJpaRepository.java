package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.dto.PlaceChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceChoiceJpaRepository extends JpaRepository<PlaceChoice, Long> {
}
