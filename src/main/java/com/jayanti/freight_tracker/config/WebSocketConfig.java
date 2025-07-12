package com.jayanti.freight_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // sets up initial handshake/connection point for websocket connections
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint and enable SockJS fallback
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    // this method sets up how messages are routed
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configure the message broker with a simple in-memory broker
        registry.enableSimpleBroker("/topic");
        // Set the prefix for messages that the application will handle
        registry.setApplicationDestinationPrefixes("/app");
    }
}
