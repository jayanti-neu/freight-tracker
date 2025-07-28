package com.jayanti.freight_tracker.dto;

import com.jayanti.freight_tracker.model.ShipmentStatus;
import com.jayanti.freight_tracker.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateShipmentRequest {

    @NotBlank(message = "Origin is required")
    private String origin;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Status is required")
    private ShipmentStatus status;

    @NotBlank(message = "Tracking number is required")
    @Size(min = 5, message = "Tracking number must be at least 5 characters long")
    private String trackingNumber;

    @Size(max = 50, message = "Carrier name cannot exceed 50 characters")
    private String carrier;

    private Priority priority;
}
