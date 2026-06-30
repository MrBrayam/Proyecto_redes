package api.proyecto.redes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Import(RedesApplicationTests.TestConfig.class)
class RedesApplicationTests {

	@TestConfiguration
	static class TestConfig {
		@Bean
		SimpMessagingTemplate simpMessagingTemplate() {
			return mock(SimpMessagingTemplate.class);
		}
	}

	@Test
	void contextLoads() {
	}

}
