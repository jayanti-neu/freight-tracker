package com.jayanti.freight_tracker.controller;

import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.repository.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;


@RestController //handles http requests and returns json responses
@RequestMapping("/api/shipments") //common base url for all endpoints
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;

    // Create a new shipment
    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        shipment.setLastUpdatedTime(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    // Get all shipments
    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    // Get shipment by ID
    @GetMapping("/{id}")
    public Optional<Shipment> getShipmentById(@PathVariable Long id) {
        return shipmentRepository.findById(id);
    }

    @GetMapping("/search")
    public Page<Shipment> searchShipments(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) ShipmentStatus status,
            Pageable pageable
    ) {
        if (origin != null && status != null) {
            return shipmentRepository.findByOriginIgnoreCaseAndStatus(origin, status, pageable);
        } else if (origin != null) {
            return shipmentRepository.findByOriginIgnoreCase(origin, pageable);
        } else if (status != null) {
            return shipmentRepository.findByStatus(status, pageable);
        } else {
            return shipmentRepository.findAll(pageable);
        }
    }

    @PutMapping("/{id}")
    public Shipment updateShipment(@PathVariable Long id, @RequestBody Shipment updatedShipment) {
        return shipmentRepository.findById(id)
                .map(shipment -> {
                    shipment.setOrigin(updatedShipment.getOrigin());
                    shipment.setDestination(updatedShipment.getDestination());
                    shipment.setStatus(updatedShipment.getStatus());
                    shipment.setLastUpdatedTime(LocalDateTime.now());
                    shipment.setTrackingNumber(updatedShipment.getTrackingNumber());
                    shipment.setCarrier(updatedShipment.getCarrier());
                    shipment.setPriority(updatedShipment.getPriority());
                    return shipmentRepository.save(shipment);
                })
                .orElseThrow(() -> new RuntimeException("Shipment not found with id " + id));
    }

    @DeleteMapping("/{id}")
    public String deleteShipment(@PathVariable Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new RuntimeException("Shipment not found with id " + id);
        }
        shipmentRepository.deleteById(id);
        return "Shipment with ID " + id + " has been deleted.";
    }

    @GetMapping("/stats")
    public ShipmentStatsDTO getShipmentStats() {
        long totalShipments = shipmentRepository.count();

        Map<ShipmentStatus, Long> statusCounts = Arrays.stream(ShipmentStatus.values())
                .collect(Collectors.toMap(
                        status -> status,
                        status -> shipmentRepository.countByStatus(status)
                ));

        String mostCommonOrigin = shipmentRepository.findMostCommonOrigin();

        return ShipmentStatsDTO.builder()
                .totalShipments(totalShipments)
                .statusCounts(statusCounts)
                .mostCommonOrigin(mostCommonOrigin)
                .build();
    }
}
