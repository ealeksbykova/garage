package com.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = {"engine", "transmission", "onBoardComputer", "wheels"})
@EqualsAndHashCode(of = "id")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "production_year", nullable = false)
    private Integer productionYear;

    @Column(length = 50)
    private String color;

    @Column(name = "mileage_km", nullable = false)
    @Builder.Default
    private Long mileageKm = 0L;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", unique = true)
    private Engine engine;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transmission_id", unique = true)
    private Transmission transmission;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "on_board_computer_id", unique = true)
    private OnBoardComputer onBoardComputer;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicle_wheel",
            joinColumns = @JoinColumn(name = "vehicle_id"),
            inverseJoinColumns = @JoinColumn(name = "wheel_id", unique = true)
    )
    @Builder.Default
    private List<Wheel> wheels = new ArrayList<>();
}