package preti.stock.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class Application {

	public static void main(String[] args){
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
	}
}
