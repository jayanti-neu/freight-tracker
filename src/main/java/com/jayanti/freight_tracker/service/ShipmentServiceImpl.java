package com.jayanti.freight_tracker.service;

import com.jayanti.freight_tracker.dto.CreateShipmentRequest;
import com.jayanti.freight_tracker.dto.UpdateShipmentRequest;
import com.jayanti.freight_tracker.exception.ShipmentNotFoundException;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import com.jayanti.freight_tracker.repository.ShipmentRepository;
import com.jayanti.freight_tracker.websocket.ShipmentStatusBroadcaster;
import com.jayanti.freight_tracker.websocket.ShipmentUpdateMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jayanti.freight_tracker.util.DateUtils.format;

@Service
public class   ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentStatusBroadcaster broadcaster;

    @Override
    public Shipment createShipment(CreateShipmentRequest request) {
        // Map fields from DTO to entity
        Shipment shipment = new Shipment();
        shipment.setOrigin(request.getOrigin());
        shipment.setDestination(request.getDestination());
        shipment.setStatus(request.getStatus());
        shipment.setTrackingNumber(request.getTrackingNumber());
        shipment.setCarrier(request.getCarrier());
        shipment.setPriority(request.getPriority());
        shipment.setLastUpdatedTime(LocalDateTime.now());

        // Save to database
        Shipment saved = shipmentRepository.save(shipment);

        // Broadcast real-time update
        broadcaster.broadcastUpdate(
                ShipmentUpdateMessage.builder()
                        .shipmentId(saved.getId())
                        .trackingNumber(saved.getTrackingNumber())
                        .status(saved.getStatus())
                        .lastUpdatedTime(format(saved.getLastUpdatedTime()))
                        .build()
        );

        return saved;
    }

    @Override
    public Shipment getShipmentByIdOrThrow(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }

    @Override
    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }

    @Override
    public Shipment updateShipment(Long id, UpdateShipmentRequest request) {
        return shipmentRepository.findById(id)
                .map(shipment -> {
                    // Update fields from the request DTO
                    // Only update fields that are not null allowing for partial updates(patch like behavior)
                    if (request.getOrigin() != null) shipment.setOrigin(request.getOrigin());
                    if (request.getDestination() != null) shipment.setDestination(request.getDestination());
                    shipment.setStatus(request.getStatus());
                    if (request.getTrackingNumber() != null) shipment.setTrackingNumber(request.getTrackingNumber());
                    if (request.getCarrier() != null) shipment.setCarrier(request.getCarrier());
                    if (request.getPriority() != null) shipment.setPriority(request.getPriority());

                    // Always refresh the last updated time
                    shipment.setLastUpdatedTime(LocalDateTime.now());

                    Shipment saved = shipmentRepository.save(shipment);

                    // Broadcast real-time update
                    broadcaster.broadcastUpdate(
                            ShipmentUpdateMessage.builder()
                                    .shipmentId(saved.getId())
                                    .trackingNumber(saved.getTrackingNumber())
                                    .status(saved.getStatus())
                                    .lastUpdatedTime(format(saved.getLastUpdatedTime()))
                                    .build()
                    );

                    return saved;
                })
                .orElseThrow(() -> new ShipmentNotFoundException(id));
    }


    @Override
    public void deleteShipment(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new ShipmentNotFoundException(id);
        }
        shipmentRepository.deleteById(id);
    }

    @Override
    public Page<Shipment> searchShipments(String origin, ShipmentStatus status, Pageable pageable) {
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

    @Override
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
