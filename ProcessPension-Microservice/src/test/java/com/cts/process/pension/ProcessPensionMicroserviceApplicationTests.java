package com.cts.process.pension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProcessPensionMicroserviceApplicationTests {

	@Test
	void contextLoads() {
		ProcessPensionMicroserviceApplication.main(new String[] {});
		assertNotNull(ProcessPensionMicroserviceApplication.class);
	}

}
