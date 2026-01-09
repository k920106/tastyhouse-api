package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceClosedDayJpaRepository extends JpaRepository<PlaceClosedDay, Long> {

    List<PlaceClosedDay> findByPlaceId(Long placeId);
}
