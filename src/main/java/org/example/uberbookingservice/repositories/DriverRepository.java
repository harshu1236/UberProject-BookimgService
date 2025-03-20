package org.example.uberbookingservice.repositories;

import jakarta.transaction.Transactional;
import org.example.uberprojectentityservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Driver d SET d.isAvailable = :availability WHERE d.id = :id")
    void updateDriverAvailability(@Param("id") Long id, @Param("availability") Boolean availability);
}
