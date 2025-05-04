package com.example.demo.controller.service;

import com.example.demo.model.Event;
import com.example.demo.service.KafkaProducerService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "spring.profiles.active=test")
@EmbeddedKafka(partitions = 1, topics = {"events"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(KafkaProducerServiceIntegrationTest.TestKafkaConfig.class) // import only test config
class KafkaProducerServiceIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private Consumer<String, Event> consumer;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Event.class.getName());

        consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(Event.class, false)
        ).createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "events");
    }

    @Test
    void testSendEventShouldSendEventSuccessfully() {
        Event event = Event.builder()
                .eventId(123L)
                .isLive(true)
                .currentScore("2-1")
                .build();

        kafkaProducerService.sendEvent(event);

        ConsumerRecord<String, Event> record = KafkaTestUtils.getSingleRecord(consumer, "events", Duration.ofSeconds(10));

        assertNotNull(record);
        assertEquals(123L, record.value().getEventId());
    }

    @TestConfiguration
    static class TestKafkaConfig {

        @Bean
        public ProducerFactory<String, Event> producerFactory(EmbeddedKafkaBroker broker) {
            Map<String, Object> props = KafkaTestUtils.producerProps(broker);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        public KafkaTemplate<String, Event> kafkaTemplate(ProducerFactory<String, Event> pf) {
            return new KafkaTemplate<>(pf);
        }
    }
}