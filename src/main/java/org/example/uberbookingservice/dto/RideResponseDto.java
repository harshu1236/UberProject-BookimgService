package org.example.uberbookingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDto {
    private Boolean response;
    private Long bookingId;
}
