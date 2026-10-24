package com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnBoardComputerRequest {
    @NotBlank
    @Size(max = 50)
    String firmwareVersion;
    @Size(max = 100)
    String cpuModel;
    @PositiveOrZero
    Integer ramMb;
}
