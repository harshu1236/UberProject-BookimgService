package org.example.uberbookingservice.controllers;

import com.netflix.discovery.EurekaClient;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.example.uberbookingservice.apis.LocationServiceApi;
import org.example.uberbookingservice.apis.UberSocketApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Configuration   // Marks this class as a Spring configuration class
public class RetrofitConfig {

    @Autowired
    public EurekaClient eurekaClient;   // Eureka client for service discovery

    /**
     * Retrieves the base URL of a registered service from Eureka Server.
     * @param serviceName The name of the service to look up.
     * @return The base URL of the service.
     */
    private String getServiceUrl(String serviceName) {
        return eurekaClient.getNextServerFromEureka(serviceName,false).getHomePageUrl();
    }

    /**
     * Creates and configures a Retrofit instance for communicating with the Location Service.
     * @return An implementation of the LocationServiceApi interface.
     */
    @Bean   // Defines this method as a Spring Bean to be managed by the application context
    public LocationServiceApi getLocationServiceApi() {
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl("UBERPROJECT-LOCATIONSERVICE"))  // Dynamically fetches base URL from Eureka
                .addConverterFactory(GsonConverterFactory.create())   // Converts JSON responses into Java objects
                .client(new OkHttpClient.Builder().build())   // Uses OkHttpClient for making network requests
                .build()
                .create(LocationServiceApi.class);    // Creates an instance of the API interface
    }

    @Bean   // Defines this method as a Spring Bean to be managed by the application context
    public UberSocketApi uberSocketApi() {
        return new Retrofit.Builder()
                .baseUrl(getServiceUrl("UBERSOCKETSERVER"))  // Dynamically fetches base URL from Eureka
                .addConverterFactory(GsonConverterFactory.create())   // Converts JSON responses into Java objects
                .client(new OkHttpClient.Builder().build())   // Uses OkHttpClient for making network requests
                .build()
                .create(UberSocketApi.class);    // Creates an instance of the API interface
    }
}
