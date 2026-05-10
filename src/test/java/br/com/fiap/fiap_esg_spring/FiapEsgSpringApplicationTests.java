package br.com.fiap.fiap_esg_spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:fiaptest;MODE=Oracle;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.flyway.enabled=false",
		"spring.sql.init.mode=never",
		"spring.jpa.hibernate.ddl-auto=none"
})
class FiapEsgSpringApplicationTests {

	@Test
	void contextLoads() {
	}

}
