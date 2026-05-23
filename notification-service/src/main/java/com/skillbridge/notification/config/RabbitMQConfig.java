package com.skillbridge.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ─── Exchange ────────────────────────────────────────────────────────────

    public static final String EXCHANGE = "skillbridge.events";

    // ─── Queues ──────────────────────────────────────────────────────────────

    public static final String QUEUE_BOOKING_CONFIRMED  = "notification.booking.confirmed";
    public static final String QUEUE_BOOKING_CANCELLED  = "notification.booking.cancelled";
    public static final String QUEUE_SESSION_REMINDER   = "notification.session.reminder";

    // ─── Routing Keys ────────────────────────────────────────────────────────

    public static final String KEY_BOOKING_CONFIRMED  = "booking.confirmed";
    public static final String KEY_BOOKING_CANCELLED  = "booking.cancelled";
    public static final String KEY_SESSION_REMINDER   = "session.reminder";

    // ─── Exchange Bean ────────────────────────────────────────────────────────

    @Bean
    public TopicExchange skillbridgeExchange() {
        // durable=true: exchange survives RabbitMQ restart
        return new TopicExchange(EXCHANGE, true, false);
    }

    // ─── Queue Beans ─────────────────────────────────────────────────────────

    @Bean
    public Queue bookingConfirmedQueue() {
        return new Queue(QUEUE_BOOKING_CONFIRMED, true);
    }

    @Bean
    public Queue bookingCancelledQueue() {
        return new Queue(QUEUE_BOOKING_CANCELLED, true);
    }

    @Bean
    public Queue sessionReminderQueue() {
        return new Queue(QUEUE_SESSION_REMINDER, true);
    }

    // ─── Binding Beans ───────────────────────────────────────────────────────

    @Bean
    public Binding bookingConfirmedBinding(Queue bookingConfirmedQueue,
                                           TopicExchange skillbridgeExchange) {
        return BindingBuilder
                .bind(bookingConfirmedQueue)
                .to(skillbridgeExchange)
                .with(KEY_BOOKING_CONFIRMED);
    }

    @Bean
    public Binding bookingCancelledBinding(Queue bookingCancelledQueue,
                                           TopicExchange skillbridgeExchange) {
        return BindingBuilder
                .bind(bookingCancelledQueue)
                .to(skillbridgeExchange)
                .with(KEY_BOOKING_CANCELLED);
    }

    @Bean
    public Binding sessionReminderBinding(Queue sessionReminderQueue,
                                          TopicExchange skillbridgeExchange) {
        return BindingBuilder
                .bind(sessionReminderQueue)
                .to(skillbridgeExchange)
                .with(KEY_SESSION_REMINDER);
    }

    // ─── Message Converter ────────────────────────────────────────────────────

    @Bean
    public MessageConverter jsonMessageConverter() {
        // Tells Spring AMQP to serialize/deserialize messages as JSON
        // instead of Java's default binary serialization
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
