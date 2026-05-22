package com.skillbridge.booking;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BookingServiceApplicationTests {

    // Mock Feign clients so no real HTTP calls are made during test context load
    @MockitoBean
    UserServiceClient userServiceClient;

    @MockitoBean
    AuthServiceClient authServiceClient;

    // Mock RabbitMQ ConnectionFactory so no real broker connection is attempted
    @MockitoBean
    ConnectionFactory connectionFactory;

    @Test
    void contextLoads() {
    }
}
