package com.jayanti.freight_tracker.repository;

import com.jayanti.freight_tracker.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository // this is a repository component - register it in application context
//handle all database operations for Shipments
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
    Page<Shipment> findByOriginIgnoreCase(String origin, Pageable pageable);
    Page<Shipment> findByOriginIgnoreCaseAndStatus(String origin, ShipmentStatus status, Pageable pageable);
    Page<Shipment> findAll(Pageable pageable);
    long countByStatus(ShipmentStatus status);

    @Query("SELECT s.origin FROM Shipment s GROUP BY s.origin ORDER BY COUNT(s) DESC LIMIT 1")
    String findMostCommonOrigin();
}
