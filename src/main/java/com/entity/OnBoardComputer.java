package com.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "on_board_computer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OnBoardComputer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firmware_version", nullable = false, length = 50)
    private String firmwareVersion;

    @Column(name = "cpu_model", length = 100)
    private String cpuModel;

    @Column(name = "ram_mb")
    private Integer ramMb;

    @Column(name = "installed_at", nullable = false)
    @Builder.Default
    private LocalDateTime installedAt = LocalDateTime.now();
}