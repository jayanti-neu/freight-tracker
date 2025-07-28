package com.jayanti.freight_tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayanti.freight_tracker.dto.CreateShipmentRequest;
import com.jayanti.freight_tracker.model.Priority;
import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ShipmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @BeforeEach
    void setup() {
        shipmentRepository.deleteAll();
    }

    // ------------------- CREATE + GET ALL -------------------
    @Test
    void createShipment_andGetAllShipments() throws Exception {
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrigin("New York");
        request.setDestination("Chicago");
        request.setStatus(ShipmentStatus.IN_TRANSIT);
        request.setTrackingNumber("TRK12345");
        request.setCarrier("FedEx");
        request.setPriority(Priority.HIGH);

        mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("New York"))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));

        mockMvc.perform(get("/api/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].origin").value("New York"));
    }

    // ------------------- GET BY ID -------------------
    @Test
    void getShipmentById_shouldReturnShipment() throws Exception {
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrigin("Los Angeles");
        request.setDestination("San Francisco");
        request.setStatus(ShipmentStatus.PENDING);
        request.setTrackingNumber("TRK98765");
        request.setCarrier("UPS");
        request.setPriority(Priority.MEDIUM);

        String response = mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long shipmentId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/shipments/" + shipmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("Los Angeles"))
                .andExpect(jsonPath("$.trackingNumber").value("TRK98765"));
    }

    // ------------------- UPDATE SHIPMENT -------------------
    @Test
    void updateShipment_shouldModifyFields() throws Exception {
        // Create initial shipment
        CreateShipmentRequest createRequest = new CreateShipmentRequest();
        createRequest.setOrigin("Seattle");
        createRequest.setDestination("Portland");
        createRequest.setStatus(ShipmentStatus.PENDING);
        createRequest.setTrackingNumber("TRK55555");
        createRequest.setCarrier("DHL");
        createRequest.setPriority(Priority.LOW);

        String response = mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long shipmentId = objectMapper.readTree(response).get("id").asLong();

        // Update request
        String updateJson = """
            {
                "origin": "Seattle",
                "destination": "Vancouver",
                "status": "IN_TRANSIT",
                "trackingNumber": "TRK55555",
                "carrier": "DHL",
                "priority": "HIGH"
            }
        """;

        mockMvc.perform(put("/api/shipments/" + shipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.destination").value("Vancouver"))
                .andExpect(jsonPath("$.status").value("IN_TRANSIT"));
    }

    // ------------------- DELETE SHIPMENT -------------------
    @Test
    void deleteShipment_shouldRemoveFromDatabase() throws Exception {
        // Create shipment
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrigin("Miami");
        request.setDestination("Orlando");
        request.setStatus(ShipmentStatus.DELIVERED);
        request.setTrackingNumber("TRKDEL123");
        request.setCarrier("FedEx");
        request.setPriority(Priority.MEDIUM);

        String response = mockMvc.perform(post("/api/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long shipmentId = objectMapper.readTree(response).get("id").asLong();

        // Delete
        mockMvc.perform(delete("/api/shipments/" + shipmentId))
                .andExpect(status().isOk())
                .andExpect(content().string("Shipment with ID " + shipmentId + " has been deleted."));

        // Confirm deletion
        mockMvc.perform(get("/api/shipments/" + shipmentId))
                .andExpect(status().isNotFound());
    }

    // ------------------- SEARCH SHIPMENTS -------------------
    @Test
    void searchShipments_shouldFilterByOriginAndStatus() throws Exception {
        // Create two shipments
        CreateShipmentRequest s1 = new CreateShipmentRequest();
        s1.setOrigin("Boston");
        s1.setDestination("Chicago");
        s1.setStatus(ShipmentStatus.IN_TRANSIT);
        s1.setTrackingNumber("TRK111");
        s1.setCarrier("FedEx");
        s1.setPriority(Priority.LOW);

        CreateShipmentRequest s2 = new CreateShipmentRequest();
        s2.setOrigin("Boston");
        s2.setDestination("New York");
        s2.setStatus(ShipmentStatus.DELIVERED);
        s2.setTrackingNumber("TRK222");
        s2.setCarrier("UPS");
        s2.setPriority(Priority.HIGH);

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s1)));

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(s2)));

        // Search by status
        mockMvc.perform(get("/api/shipments/search")
                        .param("origin", "Boston")
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("DELIVERED"));
    }

    // ------------------- STATS ENDPOINT -------------------
    @Test
    void statsEndpoint_shouldReturnCounts() throws Exception {
        CreateShipmentRequest request = new CreateShipmentRequest();
        request.setOrigin("Dallas");
        request.setDestination("Houston");
        request.setStatus(ShipmentStatus.PENDING);
        request.setTrackingNumber("TRKSTAT");
        request.setCarrier("UPS");
        request.setPriority(Priority.MEDIUM);

        mockMvc.perform(post("/api/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/shipments/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalShipments").value(1))
                .andExpect(jsonPath("$.statusCounts.PENDING").value(1));
    }
}
