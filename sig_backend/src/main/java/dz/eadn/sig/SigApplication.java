package dz.eadn.sig;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableBatchProcessing
@EnableFeignClients
public class SigApplication implements CommandLineRunner {

	/*
	 * @Autowired private BoostrapApp boostrapApp;
	 */

	public static void main(String[] args) {
		SpringApplication.run(SigApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
	}

}