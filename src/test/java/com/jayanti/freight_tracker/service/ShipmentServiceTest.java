package com.jayanti.freight_tracker.service;

import com.jayanti.freight_tracker.dto.CreateShipmentRequest;
import com.jayanti.freight_tracker.dto.UpdateShipmentRequest;
import com.jayanti.freight_tracker.dto.ShipmentStatsDTO;
import com.jayanti.freight_tracker.model.Priority;
import com.jayanti.freight_tracker.model.Shipment;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.repository.ShipmentRepository;
import com.jayanti.freight_tracker.websocket.ShipmentStatusBroadcaster;
import com.jayanti.freight_tracker.websocket.ShipmentUpdateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentStatusBroadcaster broadcaster;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -------- CREATE TEST --------
    @Test
    void testCreateShipment_SetsFieldsAndBroadcasts() {
        // Arrange: DTO with input
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrigin("New York");
        request.setDestination("Chicago");
        request.setStatus(ShipmentStatus.IN_TRANSIT);
        request.setTrackingNumber("TRK12345");
        request.setCarrier("FedEx");
        request.setPriority(Priority.HIGH);

        // Mock repository.save to use invocation to capture object
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment s = invocation.getArgument(0); // actual Shipment built by service
            s.setId(1L); // simulate DB assigning ID
            return s;
        });

        // Act
        Shipment result = shipmentService.createShipment(request);

        // Assert: check fields the SERVICE sets
        assertNotNull(result.getLastUpdatedTime());
        assertEquals("New York", result.getOrigin());
        assertEquals("TRK12345", result.getTrackingNumber());
        assertEquals(1L, result.getId());

        // Verify broadcaster called
        // broadcaster should send a ShipmentUpdateMessage and its called once
        verify(broadcaster, times(1)).broadcastUpdate(any(Shipment.class));
    }

    // -------- GET BY ID TEST --------
    @Test
    void testGetShipmentByIdOrThrow_ReturnsShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(5L);
        shipment.setOrigin("Boston");

        when(shipmentRepository.findById(5L)).thenReturn(Optional.of(shipment));

        Shipment result = shipmentService.getShipmentByIdOrThrow(5L);

        assertEquals(5L, result.getId());
        assertEquals("Boston", result.getOrigin());
    }

    @Test
    void testGetShipmentByIdOrThrow_ThrowsWhenNotFound() {
        when(shipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> shipmentService.getShipmentByIdOrThrow(99L));
    }

    // -------- UPDATE TEST --------
    @Test
    void testUpdateShipment_UpdatesFieldsAndBroadcasts() {
        // Arrange: existing shipment in DB
        Shipment existing = new Shipment();
        existing.setId(2L);
        existing.setOrigin("NY");
        existing.setDestination("Chicago");
        existing.setStatus(ShipmentStatus.PENDING);
        existing.setTrackingNumber("TRK111");

        when(shipmentRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // DTO for update
        UpdateShipmentRequest updateRequest = new UpdateShipmentRequest();
        updateRequest.setOrigin("Los Angeles");
        updateRequest.setStatus(ShipmentStatus.DELIVERED);

        // Act
        Shipment updated = shipmentService.updateShipment(2L, updateRequest);

        // Assert: origin/status updated, tracking number unchanged
        assertEquals("Los Angeles", updated.getOrigin());
        assertEquals(ShipmentStatus.DELIVERED, updated.getStatus());
        assertNotNull(updated.getLastUpdatedTime());

        verify(broadcaster, times(1)).broadcastUpdate(any(Shipment.class));
    }

    // -------- DELETE TEST --------
    @Test
    void testDeleteShipment_DeletesWhenExists() {
        when(shipmentRepository.existsById(3L)).thenReturn(true);

        shipmentService.deleteShipment(3L);

        verify(shipmentRepository, times(1)).deleteById(3L);
    }

    @Test
    void testDeleteShipment_ThrowsWhenNotFound() {
        when(shipmentRepository.existsById(4L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> shipmentService.deleteShipment(4L));
    }

    // -------- STATS TEST --------
    @Test
    void testGetShipmentStats_ReturnsCounts() {
        when(shipmentRepository.count()).thenReturn(10L);
        when(shipmentRepository.countByStatus(ShipmentStatus.IN_TRANSIT)).thenReturn(5L);
        when(shipmentRepository.countByStatus(ShipmentStatus.PENDING)).thenReturn(3L);
        when(shipmentRepository.countByStatus(ShipmentStatus.DELIVERED)).thenReturn(2L);
        when(shipmentRepository.findMostCommonOrigin()).thenReturn("New York");

        ShipmentStatsDTO stats = shipmentService.getShipmentStats();

        assertEquals(10L, stats.getTotalShipments());
        assertEquals(5L, stats.getStatusCounts().get(ShipmentStatus.IN_TRANSIT));
        assertEquals("New York", stats.getMostCommonOrigin());
    }
}
