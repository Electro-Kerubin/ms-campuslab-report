package org.campuslab.report.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.campuslab.report.dto.BookingEventMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate,
            @Value("${report.kafka.dlt-topic:report.DLT}") String dltTopic,
            @Value("${report.kafka.retry-attempts:3}") long retryAttempts) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (kafkaRecord, exception) -> new org.apache.kafka.common.TopicPartition(
                    dltTopic, kafkaRecord.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, retryAttempts));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BookingEventMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, BookingEventMessage> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, BookingEventMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public NewTopic reportDltTopic(@Value("${report.kafka.dlt-topic:report.DLT}") String dltTopic) {
        return new NewTopic(dltTopic, 3, (short) 1);
    }
}