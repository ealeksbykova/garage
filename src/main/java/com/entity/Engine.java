package com.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "engine")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class Engine {

    public enum EngineType { PETROL, DIESEL, ELECTRIC, HYBRID }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false, length = 30)
    private EngineType engineType;

    @Column(name = "volume_liters", precision = 4, scale = 2)
    private BigDecimal volumeLiters;

    @Column(nullable = false)
    private Integer horsepower;

    @Column(name = "torque_nm")
    private Integer torqueNm;

    @Column(name = "serial_number", nullable = false, unique = true, length = 64)
    private String serialNumber;
}