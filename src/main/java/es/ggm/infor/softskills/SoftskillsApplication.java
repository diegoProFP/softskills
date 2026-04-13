package es.ggm.infor.softskills;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SoftskillsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoftskillsApplication.class, args);
	}

}
