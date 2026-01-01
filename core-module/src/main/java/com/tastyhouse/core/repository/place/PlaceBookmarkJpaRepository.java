package com.tastyhouse.core.repository.place;

import com.tastyhouse.core.entity.place.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceBookmarkJpaRepository extends JpaRepository<PlaceBookmark, Long> {

    Optional<PlaceBookmark> findByPlaceIdAndMemberId(Long placeId, Long memberId);

    boolean existsByPlaceIdAndMemberId(Long placeId, Long memberId);

    void deleteByPlaceIdAndMemberId(Long placeId, Long memberId);
}