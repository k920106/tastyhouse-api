package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceJpaRepository extends JpaRepository<Place, Long> {
}
