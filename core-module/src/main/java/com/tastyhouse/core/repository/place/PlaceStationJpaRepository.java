package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceStationJpaRepository extends JpaRepository<PlaceStation, Long> {
    List<PlaceStation> findAllByOrderByStationNameAsc();
}
