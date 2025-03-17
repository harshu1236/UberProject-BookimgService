package org.example.uberbookingservice.dto;


import lombok.*;
import org.example.uberprojectentityservice.models.Driver;

import java.math.BigDecimal;
import java.util.Optional;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponseDto {
    private Long bookingId;
    private String bookingStatus;
    private Optional<Driver> driver;
    private BigDecimal price;
}
