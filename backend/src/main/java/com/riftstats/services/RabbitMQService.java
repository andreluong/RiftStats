package com.riftstats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.riftstats.dtos.MatchDTO;
import com.riftstats.enums.RegionCode;
import com.riftstats.enums.RegionGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.riftstats.utils.Constants.REGION_CODE_HEADER;
import static com.riftstats.utils.Constants.REGION_GROUP_HEADER;

@Slf4j
@Service
public class RabbitMQService {
    private final MatchProcessingService matchProcessingService;
    private final MatchFetchService matchFetchService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaType matchIdListType = objectMapper.getTypeFactory()
            .constructCollectionType(ArrayList.class, String.class);

    @Autowired
    public RabbitMQService(MatchProcessingService matchProcessingService, MatchFetchService matchFetchService) {
        this.matchProcessingService = matchProcessingService;
        this.matchFetchService = matchFetchService;
    }

    // Extract RegionGroup and RegionCode and execute given function of reading and processing a message
    private void handleMessage(String message,
                               String regionGroupValue,
                               String regionCodeValue,
                               Channel channel,
                               Message amqpMessage,
                               Function<String, BiConsumer<RegionGroup, RegionCode>> func) throws IOException {
        long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

        try {
            Optional<RegionGroup> optionalGroup = RegionGroup.fromValue(regionGroupValue);
            Optional<RegionCode> optionalCode = RegionCode.fromValue(regionCodeValue);

            if (optionalGroup.isEmpty()) {
                log.error("Unknown regionGroup={} found for message={}", regionGroupValue, message);
            } else if (optionalCode.isEmpty()) {
                log.error("Unknown regionCode={} found for message={}", regionCodeValue, message);
            } else {
                BiConsumer<RegionGroup, RegionCode> consumer = func.apply(message);
                consumer.accept(optionalGroup.get(), optionalCode.get());
            }
            channel.basicAck(deliveryTag, false);
        } catch (JsonProcessingException e) {
            log.error("Error reading value from message={} from match queue: {}", message, e.getMessage());
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            log.error("Error acknowledging message for match queue: {}", e.getMessage());
            channel.basicNack(deliveryTag, false, true);
        } catch (Exception e) {
            log.error("Error processing message for match queue: {}", e.getMessage());
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private void handleMatchMessage(String message,
                                    String regionGroupValue,
                                    String regionCodeValue,
                                    Channel channel,
                                    Message amqpMessage) {
        try {
            MatchDTO match = objectMapper.readValue(message, MatchDTO.class);
            handleMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage,
                    msg -> (group, code) -> matchProcessingService.processMatch(match, code));
        } catch (Exception e) {
            log.error("Error handling match message for regionGroup={} regionCode={}: {}",
                    regionGroupValue, regionCodeValue, e.getMessage());
        }
    }

    /* Listen to each regional match queue individually for per-queue concurrency */

    @RabbitListener(queues = "match.queue.americas", concurrency = "1-5")
    public void listenAmericasMatchMessage(String message,
                                           @Header(REGION_GROUP_HEADER) String regionGroupValue,
                                           @Header(REGION_CODE_HEADER) String regionCodeValue,
                                           Channel channel,
                                           Message amqpMessage) {
        handleMatchMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage);
    }

    @RabbitListener(queues = "match.queue.asia", concurrency = "1-5")
    public void listenAsiaMatchMessage(String message,
                                           @Header(REGION_GROUP_HEADER) String regionGroupValue,
                                           @Header(REGION_CODE_HEADER) String regionCodeValue,
                                           Channel channel,
                                           Message amqpMessage) {
        handleMatchMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage);
    }

    @RabbitListener(queues = "match.queue.europe", concurrency = "1-5")
    public void listenEuropeMatchMessage(String message,
                                       @Header(REGION_GROUP_HEADER) String regionGroupValue,
                                       @Header(REGION_CODE_HEADER) String regionCodeValue,
                                       Channel channel,
                                       Message amqpMessage) {
        handleMatchMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage);
    }

    @RabbitListener(queues = "match.queue.sea", concurrency = "1-5")
    public void listenSeaMatchMessage(String message,
                                         @Header(REGION_GROUP_HEADER) String regionGroupValue,
                                         @Header(REGION_CODE_HEADER) String regionCodeValue,
                                         Channel channel,
                                         Message amqpMessage) {
        handleMatchMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage);
    }

    @RabbitListener(queues = "player.queue", concurrency = "1-2")
    public void listenPlayerMessage(String message,
                                     @Header(REGION_GROUP_HEADER) String regionGroupValue,
                                     @Header(REGION_CODE_HEADER) String regionCodeValue,
                                     Channel channel,
                                     Message amqpMessage) {
        try {
            List<String> matchIds = objectMapper.readValue(message, matchIdListType);
            handleMessage(message, regionGroupValue, regionCodeValue, channel, amqpMessage,
                    msg -> (group, code) -> matchFetchService.fetchMatches(matchIds, group, code));
        } catch (Exception e) {
            log.error("Error handling player message for regionGroup={} regionCode={}: {}",
                    regionGroupValue, regionCodeValue, e.getMessage());
        }
    }
}
