package cn.qaq.valveapi;

import cn.qaq.valveapi.controller.ApiController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableFeignClients
@MapperScan("cn.qaq.valveapi.dao")
public class ValveapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ValveapiApplication.class, args);
	}

}
