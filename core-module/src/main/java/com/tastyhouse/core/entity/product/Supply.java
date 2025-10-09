package com.tastyhouse.core.entity.product;

import com.tastyhouse.core.entity.BaseEntity;
import jakarta.persistence.*;
    import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SUPPLY")
@Getter
@Setter
public class Supply extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 공급사명
}
