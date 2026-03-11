package com.example.notification;

import com.example.notification.config.TestOAuth2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestOAuth2Config.class)
class NotificationApplicationTests {

	@Test
	void contextLoads() {
	}

}
