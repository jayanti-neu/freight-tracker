package com.jayanti.freight_tracker.controller;

import com.jayanti.freight_tracker.dto.CreateShipmentRequest;
import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import com.jayanti.freight_tracker.dto.UpdateShipmentRequest;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.service.ShipmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping
    public Shipment createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        return shipmentService.createShipment(request);
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentService.getAllShipments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        Shipment shipment = shipmentService.getShipmentByIdOrThrow(id); // create this method
        return ResponseEntity.ok(shipment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShipmentRequest request) {
        Shipment updated = shipmentService.updateShipment(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok("Shipment with ID " + id + " has been deleted.");
    }

    @GetMapping("/search")
    public Page<Shipment> searchShipments(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) ShipmentStatus status,
            Pageable pageable
    ) {
        return shipmentService.searchShipments(origin, status, pageable);
    }

    @GetMapping("/stats")
    public ShipmentStatsDTO getShipmentStats() {
        return shipmentService.getShipmentStats();
    }
}
