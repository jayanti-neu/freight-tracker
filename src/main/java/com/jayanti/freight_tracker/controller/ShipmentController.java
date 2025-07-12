package com.jayanti.freight_tracker.controller;

import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.repository.ShipmentRepository;
import com.jayanti.freight_tracker.websocket.ShipmentStatusBroadcaster;
import com.jayanti.freight_tracker.websocket.ShipmentUpdateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController //handles http requests and returns json responses
@RequestMapping("/api/shipments") //common base url for all endpoint
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusBroadcaster broadcaster;

    @PostMapping
    public Shipment createShipment(@RequestBody Shipment shipment) {
        shipment.setLastUpdatedTime(LocalDateTime.now());
        Shipment saved = shipmentRepository.save(shipment);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        // Broadcast update via WebSocket
        broadcaster.broadcastUpdate(
                ShipmentUpdateMessage.builder()
                        .shipmentId(saved.getId())
                        .trackingNumber(saved.getTrackingNumber())
                        .status(saved.getStatus())
                        .lastUpdatedTime(saved.getLastUpdatedTime().format(formatter))
                        .build()
        );

        return saved;
    }

    @GetMapping
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Shipment> getShipmentById(@PathVariable Long id) {
        return shipmentRepository.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @RequestBody Shipment updatedShipment) {
        return shipmentRepository.findById(id)
                .map(shipment -> {
                    shipment.setOrigin(updatedShipment.getOrigin());
                    shipment.setDestination(updatedShipment.getDestination());
                    shipment.setStatus(updatedShipment.getStatus());
                    shipment.setLastUpdatedTime(LocalDateTime.now());
                    shipment.setTrackingNumber(updatedShipment.getTrackingNumber());
                    shipment.setCarrier(updatedShipment.getCarrier());
                    shipment.setPriority(updatedShipment.getPriority());
                    Shipment saved = shipmentRepository.save(shipment);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

                    // Broadcast update
                    broadcaster.broadcastUpdate(
                            ShipmentUpdateMessage.builder()
                                    .shipmentId(saved.getId())
                                    .trackingNumber(saved.getTrackingNumber())
                                    .status(saved.getStatus())
                                    .lastUpdatedTime(saved.getLastUpdatedTime().format(formatter))
                                    .build()
                    );

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public String deleteShipment(@PathVariable Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new RuntimeException("Shipment not found with id " + id);
        }
        shipmentRepository.deleteById(id);
        return "Shipment with ID " + id + " has been deleted.";
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
