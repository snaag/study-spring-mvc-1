package com.example.servlet.basic.request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 1. 파라미터 전송 기능
 * http://localhost:8080/request-param?username=hello&age=20
 */
@WebServlet(name = "requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printParam(request, response);
    }

    private static void printParam(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("[전체 쿼리 파라미터 조회] - start");

        System.out.println("@방법 1 - name 으로 조회");
        request.getParameterNames().asIterator().forEachRemaining(paramName ->
                System.out.println("paramName = " + paramName + ":" + request.getParameter(paramName)));

        System.out.println("@방법 2 - name 에 대응되는 값이 여러개인 경우");
        // http://localhost:8080/request-param?username=name1&username=name100
        String key = "username";
        String[] usernames = request.getParameterValues(key);
        for (String username : usernames) {
            System.out.println("username = " + username);
        }

        System.out.println("[전체 쿼리 파라미터 조회] - end");

        response.getWriter().write("OK");
    }
}
