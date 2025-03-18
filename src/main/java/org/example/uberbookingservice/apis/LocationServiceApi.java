package org.example.uberbookingservice.apis;

import org.example.uberbookingservice.dto.DriverLocationDto;
import org.example.uberbookingservice.dto.NearByDriverRequestDto;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

@Component
public interface LocationServiceApi {

    @POST("/api/location/nearby/drivers")
    Call<DriverLocationDto[]> getNearByDrivers(@Body NearByDriverRequestDto nearByDriverRequestDto);
}
