package com.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransmissionRequest {
    @NotBlank
    @Pattern(regexp = "MANUAL|AUTOMATIC|CVT|ROBOTIC")
    String transmissionType;
    @NotNull
    @PositiveOrZero
    Integer gearsCount;
    @NotBlank
    @Size(max = 64)
    String serialNumber;
}
