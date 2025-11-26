package mini.minishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MinishopApplication {

    public static void main(String[] args) {
        System.out.println("Minishop 애플리케이션 시작!");
        SpringApplication.run(MinishopApplication.class, args);
    }
}
