package com.example.servlet.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// Servlet 은 HttpServlet 을 상속받아야 함
@WebServlet(name = "helloServlet", urlPatterns = "/hello") // /hello 로 오면 요게 실행되는 것
public class HelloServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("request = " + request); // org.apache.catalina.connector.RequestFacade
        System.out.println("response = " + response); // org.apache.catalina.connector.ResponseFacade
        // RequestFacade 는 HttpServletRequest 에 대한 WAS 들의 구현체 중 하나. 지금은 Tomcat 을 쓰고 있으므로, RequestFacade 는 Tomcat 의 구현체
        // ResponseFacade 는 HttpServletResponse 에 대한 WAS 들의 구현체 중 하나. 지금은 Tomcat 을 쓰고 있으므로, ResponseFacade 는 Tomcat 의 구현체

        String username = request.getParameter("username");
        response.setContentType("text/plain"); // 보낼 데이터의 타입
        response.setCharacterEncoding("utf-8"); // 인코딩 정보, 옛날 시스템이 아니라면 UTF-8 을 서야 함
        response.getWriter().write("hello " + username); // body 에 담아서 보내줌
    }
}
