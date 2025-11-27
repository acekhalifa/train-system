package com.esl.academy.api.integration.tests.base;

import com.esl.academy.api.TCMPApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class TCMPApplicationTests {

	//@Test
	void contextLoads() {
		var modules = ApplicationModules.of(TCMPApplication.class);
		for (var m : modules)
			System.out.println(m.getName() + ":" + m.getBasePackage());
		modules.verify();
	}

}
