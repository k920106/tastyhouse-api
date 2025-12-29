package com.tastyhouse.core.entity.place;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PLACE_BOOKMARK")
public class PlaceBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;
}
