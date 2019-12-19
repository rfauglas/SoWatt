package org.sowatt.sowattweb;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest()
@Disabled("can not load 2 contexts on same system")
class SowattWebApplicationTests {

	@Test
	void contextLoads() {
	}

}
