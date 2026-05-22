package com.skillbridge.booking;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import com.skillbridge.booking.event.BookingEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration",
        "auth.service.url=http://localhost:8081",
        "user.service.url=http://localhost:8082"
})
class BookingServiceApplicationTests {

    @MockitoBean
    UserServiceClient userServiceClient;

    @MockitoBean
    AuthServiceClient authServiceClient;

    @MockitoBean
    BookingEventPublisher bookingEventPublisher;

    @Test
    void contextLoads() {
    }
}
