package com.skillbridge.booking;

import com.skillbridge.booking.client.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BookingServiceApplicationTests {

	@MockitoBean
	UserServiceClient userServiceClient;

	@Test
	void contextLoads() {
	}

}
