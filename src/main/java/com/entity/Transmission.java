package com.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "transmission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(of = "id")
public class Transmission {

    public enum TransmissionType { MANUAL, AUTOMATIC, CVT, ROBOTIC }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission_type", nullable = false, length = 30)
    private TransmissionType transmissionType;

    @Column(name = "gears_count", nullable = false)
    private Integer gearsCount;

    @Column(name = "serial_number", nullable = false, unique = true, length = 64)
    private String serialNumber;
}