package com.jayanti.freight_tracker.websocket;

import com.jayanti.freight_tracker.model.ShipmentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentUpdateMessage {
    private Long shipmentId;
    private String trackingNumber;
    private ShipmentStatus status;
    private String lastUpdatedTime;
}
