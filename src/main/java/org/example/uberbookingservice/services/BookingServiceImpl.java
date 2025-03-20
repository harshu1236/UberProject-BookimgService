package org.example.uberbookingservice.services;

import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberbookingservice.apis.UberSocketApi;
import org.example.uberbookingservice.dto.*;
import org.example.uberbookingservice.repositories.BookingRepository;
import org.example.uberbookingservice.repositories.DriverRepository;
import org.example.uberbookingservice.repositories.PassengerRepository;
import org.example.uberprojectentityservice.models.Booking;
import org.example.uberprojectentityservice.models.BookingStatus;
import org.example.uberprojectentityservice.models.Driver;
import org.example.uberprojectentityservice.models.Passenger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{

    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate;
//    private static final String LOCATION_SERVICE_URL = "http://localhost:7476";
    private final LocationServiceApi locationServiceApi;
    private final DriverRepository driverRepository;
    private final UberSocketApi uberSocketApi;

    public BookingServiceImpl(PassengerRepository passengerRepository, BookingRepository bookingRepository, LocationServiceApi locationServiceApi, DriverRepository driverRepository,UberSocketApi uberSocketApi) {
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.restTemplate = new RestTemplate();
        this.locationServiceApi = locationServiceApi;
        this.driverRepository = driverRepository;
        this.uberSocketApi = uberSocketApi;
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

        Booking newbooking = bookingRepository.save(booking);

        //  make an api call to location service to fetch some nearby drivers

        NearByDriverRequestDto request = NearByDriverRequestDto.builder()
                .latitude(bookingDetails.getStartLocation().getLatitude())
                .longitude(bookingDetails.getStartLocation().getLongitude())
                .build();

        processNearByDriversAsync(request,bookingDetails.getPassengerId(),newbooking.getId());
//
//        ResponseEntity<DriverLocationDto[]> result = restTemplate.postForEntity(LOCATION_SERVICE_URL + "/api/location/nearby/drivers",request, DriverLocationDto[].class);
//
//        if(result.getStatusCode().is2xxSuccessful() && result.getBody() != null){
//            List<DriverLocationDto> driverLocation = Arrays.asList(result.getBody());
//            driverLocation.forEach(driverLocationDto -> {
//                System.out.println(driverLocationDto.getDriverId() + " Latitude : " + driverLocationDto.getLatitude() + " Longitude : " + driverLocationDto.getLongitude());
//            });
//        }

        return CreateBookingResponseDto.builder()
                .bookingId(newbooking.getId())
                .bookingStatus(newbooking.getBookingStatus().name())
//                .driver(Optional.of(newbooking.getDriver()))
                .build();
    }

    @Override
    public UpdateBookingResponseDto updateBooking(Long bookingId, UpdateBookingRequestDto updateBookingRequestDto) {

        Optional<Driver> driver = driverRepository.findById(updateBookingRequestDto.getDriverId().get());

        if(driver.isPresent() && driver.get().isAvailable()) {
            Optional<Booking> booking = bookingRepository.findById(bookingId);
            bookingRepository.updateBookingStatusAndDriverById(bookingId, BookingStatus.SCHEDULED, driver.get());
            driverRepository.updateDriverAvailability(driver.get().getId(),false);
            return UpdateBookingResponseDto.builder()
                    .status(booking.get().getBookingStatus())
                    .bookingId(bookingId)
                    .driver(driver)
                    .build();
        }
        return null;
    }

    private void processNearByDriversAsync(NearByDriverRequestDto nearByDriverRequestDto,Long passengerId,Long bookingId) {
        Call<DriverLocationDto[]> call = locationServiceApi.getNearByDrivers(nearByDriverRequestDto);

        call.enqueue(new Callback<DriverLocationDto[]>() {
            @Override
            public void onResponse(Call<DriverLocationDto[]> call, Response<DriverLocationDto[]> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<DriverLocationDto> driverLocation = Arrays.asList(response.body());
                    driverLocation.forEach(driverLocationDto -> {
                        System.out.println(driverLocationDto.getDriverId() + " Latitude : " + driverLocationDto.getLatitude() + " Longitude : " + driverLocationDto.getLongitude());
                    });
                    raiseRideRequestAsync(RideRequestDto.builder().bookingId(bookingId).passengerId(passengerId).build());
                }else {
                    System.out.println("Error in the process near by driver : " + response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationDto[]> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void raiseRideRequestAsync(RideRequestDto rideRequestDto) {
        Call<Boolean> call = uberSocketApi.raiseRideRequest(rideRequestDto);

        call.enqueue(new Callback<Boolean>() {

            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Boolean result = response.body();
                    System.out.println("Driver response is : " + result);
                }else {
                    System.out.println("Error in the raise: " + response.code() + " : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
