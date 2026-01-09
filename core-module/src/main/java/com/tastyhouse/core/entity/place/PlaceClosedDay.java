package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLACE_CLOSED_DAY")
public class PlaceClosedDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "closed_day_type", nullable = false, length = 50, columnDefinition = "VARCHAR(50)")
    private ClosedDayType closedDayType;
}
