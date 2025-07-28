package com.jayanti.freight_tracker.dto;

import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.model.Priority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateShipmentRequest {

    private String origin;          // Optional

    private String destination;     // Optional

    @NotNull(message = "Status cannot be null")
    private ShipmentStatus status;  // Required

    private String trackingNumber;  // Optional

    private String carrier;         // Optional

    private Priority priority;      // Optional
}
