package com.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OnBoardComputerRequest {
    String firmwareVersion;
    String cpuModel;
    Integer ramMb;
}