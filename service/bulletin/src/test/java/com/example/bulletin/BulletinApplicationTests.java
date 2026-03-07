package com.example.bulletin;

import com.example.bulletin.config.TestOAuth2Config;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestOAuth2Config.class)
class BulletinApplicationTests {

	@Test
	void contextLoads() {
	}

}
