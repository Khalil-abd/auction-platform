package com.auction.notification.consumer;

import com.auction.notification.event.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BidEventConsumer {

    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "${app.kafka.bids-topic}", groupId = "notification-group")
    public void consumeBidPlacedEvent(BidPlacedEvent event) {
        log.info("Received Kafka BidPlacedEvent for auction: {} with amount: {}", event.getAuctionId(), event.getAmount());

        // Dynamic routing destination string
        String destination = "/topic/auction/" + event.getAuctionId();

        // Broadcast the data payload to every user listening to this specific auction channel
        messagingTemplate.convertAndSend(destination, event);
        log.debug("Broadcasted bid details out over WebSocket destination: {}", destination);
    }
}