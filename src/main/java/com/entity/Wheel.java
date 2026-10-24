package com.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wheel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class Wheel {

    public enum Position { FRONT_LEFT, FRONT_RIGHT, REAR_LEFT, REAR_RIGHT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Position position;

    @Column(name = "radius_inch", nullable = false, precision = 4, scale = 1)
    private BigDecimal radiusInch;

    @Column(name = "tire_brand", length = 100)
    private String tireBrand;

    @Column(name = "tire_model", length = 100)
    private String tireModel;

    @Column(name = "serial_number", nullable = false, unique = true, length = 64)
    private String serialNumber;

    @Column(name = "installed_at", nullable = false)
    @Builder.Default
    private LocalDateTime installedAt = LocalDateTime.now();
}
