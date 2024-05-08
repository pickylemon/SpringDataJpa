package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository")
//스프링 부트를 사용하면 이 어노테이션도 생략 가능
//보통 이 JavaConfig 파일은 최상위 디렉토리에 위치하므로 부트가 패키지 알아서 스캔
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
