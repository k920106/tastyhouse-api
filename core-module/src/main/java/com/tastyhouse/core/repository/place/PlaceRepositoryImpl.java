package com.tastyhouse.core.repository.place;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tastyhouse.core.entity.place.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import static com.tastyhouse.core.entity.place.QPlace.place;

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
}
