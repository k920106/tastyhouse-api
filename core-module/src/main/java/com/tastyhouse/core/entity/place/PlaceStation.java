package com.tastyhouse.core.entity.place;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLACE_STATION")
public class PlaceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_name", nullable = false)
    private String stationName;
}
