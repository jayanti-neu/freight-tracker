package com.jayanti.freight_tracker.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShipmentStatusBroadcaster {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcasts a shipment update message to all connected clients.
     *
     * @param message The shipment update message to broadcast.
     */
    // This method uses SimpMessagingTemplate to send messages to a specific topic
    public void broadcastUpdate(ShipmentUpdateMessage message) {
        System.out.println("Broadcasting: " + message);
        messagingTemplate.convertAndSend("/topic/shipments", message);
    }
}
