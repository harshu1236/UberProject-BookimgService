package org.example.uberbookingservice.dto;

import lombok.*;
import org.example.uberbookingservice.models.ExactLocation;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {
    private Long passengerId;
    private ExactLocation startLocation;
    private ExactLocation endLocation;
    private List<Long> driverIds;
    private Long bookingId;
}
