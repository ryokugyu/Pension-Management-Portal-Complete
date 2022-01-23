package com.cts.pensionerDetail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cts.pensionerDetail.service.PensionerDetailService;

@SpringBootTest
class PensionerDetailMicroserviceApplicationTests {

	@Autowired
	PensionerDetailService pensionerDetailService;
	
	@Test
	void contextLoads() {
		PensionerDetailMicroserviceApplication.main(new String[] {});
		assertThat(pensionerDetailService).isNotNull();
	}
}
