package com.tastyhouse.core.repository.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.Place;
import com.tastyhouse.core.entity.place.dto.BestPlaceItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.tastyhouse.core.entity.place.QPlace.place;
import static com.tastyhouse.core.entity.place.QStation.station;
import static com.tastyhouse.core.entity.place.QPlaceImage.placeImage;
import static com.tastyhouse.core.entity.place.QPlaceTag.placeTag;
import static com.tastyhouse.core.entity.place.QTag.tag;

@Repository
@RequiredArgsConstructor
public class PlaceRepositoryImpl implements PlaceRepository {

    private final PlaceJpaRepository placeJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Place> findNearbyPlaces(BigDecimal latitude, BigDecimal longitude) {
        // 200m를 위도/경도 차이로 변환 (대략적인 계산)
        // 1도 = 약 111km, 200m = 0.0018도 정도
        double distanceInMeters = 200.0;
        double degreeDistance = distanceInMeters / 111000.0;
        BigDecimal latDiff = BigDecimal.valueOf(degreeDistance);
        BigDecimal lonDiff = BigDecimal.valueOf(degreeDistance);

        return queryFactory.select(place)
                           .from(place)
                           .where(
                               place.latitude.between(latitude.subtract(latDiff), latitude.add(latDiff))
                              .and(place.longitude.between(longitude.subtract(lonDiff), longitude.add(lonDiff)))
                           )
                           .fetch();
    }

    @Override
    public Page<BestPlaceItemDto> findBestPlaces(Pageable pageable) {
        // 1. 전체 개수 조회
        long total = queryFactory
            .selectFrom(place)
            .where(place.rating.isNotNull())
            .fetchCount();

        if (total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. 평점 기준 페이징 처리된 Place 조회
        List<Place> pagedPlaces = queryFactory
            .selectFrom(place)
            .where(place.rating.isNotNull())
            .orderBy(place.rating.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        if (pagedPlaces.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<Long> placeIds = pagedPlaces.stream()
            .map(Place::getId)
            .collect(Collectors.toList());

        // 3. Place별 Station 정보 조회
        var stationMap = queryFactory
            .select(place.id, station.stationName)
            .from(place)
            .join(station).on(station.id.eq(place.stationId))
            .where(place.id.in(placeIds))
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(place.id),
                tuple -> tuple.get(station.stationName)
            ));

        // 4. Place별 썸네일 이미지 조회
        var imageMap = queryFactory
            .select(placeImage.placeId, placeImage.imageUrl)
            .from(placeImage)
            .where(placeImage.placeId.in(placeIds)
                .and(placeImage.isThumbnail.eq(true)))
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(placeImage.placeId),
                tuple -> tuple.get(placeImage.imageUrl)
            ));

        // 5. Place별 태그 조회
        var tagsMap = queryFactory
            .select(placeTag.placeId, tag.tagName)
            .from(placeTag)
            .join(tag).on(tag.id.eq(placeTag.tagId))
            .where(placeTag.placeId.in(placeIds))
            .fetch()
            .stream()
            .collect(Collectors.groupingBy(
                tuple -> tuple.get(placeTag.placeId),
                Collectors.mapping(
                    tuple -> tuple.get(tag.tagName),
                    Collectors.toList()
                )
            ));

        // 6. 결과 조합
        List<BestPlaceItemDto> content = pagedPlaces.stream()
            .map(p -> new BestPlaceItemDto(
                p.getId(),
                p.getPlaceName(),
                stationMap.get(p.getId()),
                p.getRating(),
                imageMap.get(p.getId()),
                tagsMap.getOrDefault(p.getId(), List.of())
            ))
            .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }
}
