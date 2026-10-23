package com.repository;

import com.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("""
        SELECT DISTINCT v FROM Vehicle v
        LEFT JOIN FETCH v.engine
        LEFT JOIN FETCH v.transmission
        LEFT JOIN FETCH v.onBoardComputer
        LEFT JOIN FETCH v.wheels
        WHERE v.id = :id
        """)
    Optional<Vehicle> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT v FROM Vehicle v
        LEFT JOIN FETCH v.engine
        LEFT JOIN FETCH v.transmission
        LEFT JOIN FETCH v.onBoardComputer
        LEFT JOIN FETCH v.wheels
        """)
    List<Vehicle> findAllWithDetails();

    @Query("SELECT v FROM Vehicle v JOIN v.wheels w WHERE w.id = :wheelId")
    Optional<Vehicle> findByWheelId(@Param("wheelId") Long wheelId);
}