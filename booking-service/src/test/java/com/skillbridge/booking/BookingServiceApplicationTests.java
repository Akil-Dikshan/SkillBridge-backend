package com.skillbridge.booking;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import com.skillbridge.booking.event.BookingEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
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
