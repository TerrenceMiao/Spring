package org.paradise.net.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = HttpclientApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
class HttpclientApplicationTests {

	@LocalServerPort
	int port;

	@Test
	void contextLoads() {
		log.info("This is Http Client runs on http://localhost:" + port + "/");
	}
}
