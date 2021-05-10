package org.paradise.net.httpclient

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = HttpclientApplication.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class HttpclientApplicationSpec extends Specification {

	@LocalServerPort
	int port

	def "contextLoads"() {

		given:
		println "This is Http Client runs on http://localhost:" + port + "/"
	}
}
