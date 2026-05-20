package com.skillbridge.booking;

import com.skillbridge.booking.client.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BookingServiceApplicationTests {

	@MockBean
	UserServiceClient userServiceClient;

	@Test
	void contextLoads() {
	}

}
