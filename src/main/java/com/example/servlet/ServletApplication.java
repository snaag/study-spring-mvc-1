package com.example.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

// 현재 내 패키지를 포함해서 하위 패키지를 다 뒤져서
// 자동으로 Servlet 을 등록할 수 있도록 해줌
@ServletComponentScan // * Servlet 자동 등록
@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

}
