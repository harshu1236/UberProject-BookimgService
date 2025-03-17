package org.example.uberbookingservice.repositories;

import org.example.uberprojectentityservice.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepostory extends JpaRepository<Booking, Long> {
}
