package com.jayanti.freight_tracker.service;

import com.jayanti.freight_tracker.dto.CreateShipmentRequest;
import com.jayanti.freight_tracker.dto.UpdateShipmentRequest;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {
    Shipment createShipment(CreateShipmentRequest shipment);
    Shipment getShipmentByIdOrThrow(Long id);
    List<Shipment> getAllShipments();
    Shipment updateShipment(Long id, UpdateShipmentRequest updatedShipment);
    void deleteShipment(Long id);
    Page<Shipment> searchShipments(String origin, ShipmentStatus status, Pageable pageable);
    ShipmentStatsDTO getShipmentStats();
}
