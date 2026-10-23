package com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnBoardComputerResponse {
    Long id;
    String firmwareVersion;
    String cpuModel;
    Integer ramMb;
    LocalDateTime installedAt;
}