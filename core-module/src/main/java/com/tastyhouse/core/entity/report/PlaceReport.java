package com.tastyhouse.core.entity.report;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "PLACE_REPORT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "address_detail", length = 500)
    private String addressDetail;

    public PlaceReport(String businessName, String address, String addressDetail) {
        this.businessName = businessName;
        this.address = address;
        this.addressDetail = addressDetail;
    }

    public static PlaceReport of(String businessName, String address, String addressDetail) {
        return new PlaceReport(businessName, address, addressDetail);
    }
}
