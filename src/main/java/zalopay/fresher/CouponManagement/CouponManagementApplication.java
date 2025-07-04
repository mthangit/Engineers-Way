package zalopay.fresher.CouponManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class CouponManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponManagementApplication.class, args);
	}
}
