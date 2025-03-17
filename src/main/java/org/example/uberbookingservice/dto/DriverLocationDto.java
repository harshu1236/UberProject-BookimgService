package org.example.uberbookingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocationDto {
    private String driverId;
    private Double longitude;
    private Double latitude;
}
