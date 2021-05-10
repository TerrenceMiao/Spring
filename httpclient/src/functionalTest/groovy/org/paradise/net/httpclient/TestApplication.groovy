package org.paradise.net.httpclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ActiveProfiles

@SpringBootApplication
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [HttpclientApplication]),
        basePackageClasses = [TestApplication]
)
@ActiveProfiles('test')
@PropertySource("classpath:application-functional.properties")
class TestApplication {

}
