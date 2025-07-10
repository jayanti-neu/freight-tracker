package com.jayanti.freight_tracker.dto;

import com.jayanti.freight_tracker.model.ShipmentStatus;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentStatsDTO {
    private long totalShipments;
    private Map<ShipmentStatus, Long> statusCounts;
    private String mostCommonOrigin;
}
