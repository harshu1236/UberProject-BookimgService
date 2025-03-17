package org.example.uberbookingservice.services;

import org.example.uberbookingservice.dto.CreateBookingDto;
import org.example.uberbookingservice.dto.CreateBookingResponseDto;
import org.example.uberbookingservice.dto.DriverLocationDto;
import org.example.uberbookingservice.dto.NearByDriverRequestDto;
import org.example.uberbookingservice.repositories.BookingRepostory;
import org.example.uberbookingservice.repositories.PassengerRepository;
import org.example.uberprojectentityservice.models.Booking;
import org.example.uberprojectentityservice.models.BookingStatus;
import org.example.uberprojectentityservice.models.Passenger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    private final PassengerRepository passengerRepository;
    private final BookingRepostory bookingRepostory;
    private final RestTemplate restTemplate;
    private static final String LOCATION_SERVICE_URL = "http://localhost:7476";

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepostory bookingRepostory) {
        this.passengerRepository = passengerRepository;
        this.bookingRepostory = bookingRepostory;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public CreateBookingResponseDto createBooking(CreateBookingDto bookingDetails){
        Optional<Passenger> passenger = passengerRepository.findById(bookingDetails.getPassengerId());

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.ASSIGNING_DRIVER)
                .startLocation(bookingDetails.getStartLocation())
                .endLocation(bookingDetails.getEndLocation())
                .startTime(new Date())
                .price(BigDecimal.ZERO)
                .passenger(passenger.get())
                .build();

        Booking newbooking = bookingRepostory.save(booking);

        //  make an api call to location service to fetch some nearby drivers

        NearByDriverRequestDto request = NearByDriverRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE_URL + "/api/location/nearby/drivers",request, DriverLocationDto[].class);

        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null){
            List<DriverLocationDto> driverLocation = Arrays.asList(result.getBody());
            driverLocation.forEach(driverLocationDto -> {
                System.out.println(driverLocationDto.getDriverId() + " Latitude : " + driverLocationDto.getLatitude() + " Longitude : " + driverLocationDto.getLongitude());
            });
        }

        return CreateBookingResponseDto.builder()
                .bookingId(newbooking.getId())
                .bookingStatus(newbooking.getBookingStatus().name())
//                .driver(Optional.of(newbooking.getDriver()))
                .build();
    }
}
