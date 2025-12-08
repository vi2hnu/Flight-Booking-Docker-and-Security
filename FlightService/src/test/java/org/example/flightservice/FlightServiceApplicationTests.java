package org.example.flightservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
class FlightServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
