package com.skillbridge.booking;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import com.skillbridge.booking.event.BookingEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
)
class BookingServiceApplicationTests {

    // Mock Feign clients — no real HTTP calls during context load
    @MockitoBean
    UserServiceClient userServiceClient;

    @MockitoBean
    AuthServiceClient authServiceClient;

    // Mock event publisher — excludes RabbitAutoConfiguration above means
    // RabbitTemplate is not in context, so the real publisher cannot be wired.
    // Mocking it here satisfies BookingService's dependency cleanly.
    @MockitoBean
    BookingEventPublisher bookingEventPublisher;

    @Test
    void contextLoads() {
    }
}
