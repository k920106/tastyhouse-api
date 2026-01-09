package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceBreakTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceBreakTimeJpaRepository extends JpaRepository<PlaceBreakTime, Long> {

    List<PlaceBreakTime> findByPlaceIdOrderByDayType(Long placeId);
}
