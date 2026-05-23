package com.skillbridge.notification;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        excludeAutoConfiguration = RabbitAutoConfiguration.class
)
@ActiveProfiles("test")
class NotificationServiceApplicationTests {

    @MockBean
    JavaMailSender javaMailSender;

    @Test
    void contextLoads() {
    }
}
