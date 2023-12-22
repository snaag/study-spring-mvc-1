package com.example.servlet.basic.request;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "requestBodyStringServlet", urlPatterns = "/request-body-string")
public class RequestBodyStringServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // * body -> bytecode -> string
        ServletInputStream inputStream = request.getInputStream(); // body 의 내용을 bytecode 로 얻을 수 있음 (body -> bytecode)
        // ! 이 때 encoding 명시 필수
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); // bytecode 를 string 으로 변환함 (bytecode -> string)

        System.out.println("messageBody = " + messageBody);

        response.getWriter().write("ok");
    }
}
