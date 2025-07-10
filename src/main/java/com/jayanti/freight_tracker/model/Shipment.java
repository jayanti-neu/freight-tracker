package com.jayanti.freight_tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;

    private String destination;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private LocalDateTime lastUpdatedTime;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    private String carrier;

    @Enumerated(EnumType.STRING)
    private Priority priority;
}
