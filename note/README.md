# 섹션2. 서블릿
## 섹션2.1 Project 만들기

- https://start.spring.io/
- 각종 설정들 
  - `Project` Gradle-Groovy
  - `Language` Java
  - `Spring Boot` 3.x.x
  - `Packaging` **_War_**
    - JSP 를 쓰려면 War 를 사용해야 함 
    - _일반적으로는 톰캣이 내장되어 바로 실행시킬 수 있는 Jar 를 사용하나, 강의에서는 예외로 War 사용함_  
  - `Java` 17
  - `Dependencies`
    - Lombok
    - Spring Web

## 섹션2.2 ServletComponentScan 
```java
// 현재 내 패키지를 포함해서 하위 패키지를 다 뒤져서
// 자동으로 Servlet 을 등록할 수 있도록 해줌
@ServletComponentScan // * Servlet 자동 등록
@SpringBootApplication
public class ServletApplication {
	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}
}
```
- 현재 내 패키지를 포함해서 하위 패키지를 다 뒤져서, 자동으로 Servlet 을 등록할 수 있도록 해줌

## 섹션2.3 Servlet 만들기
```java
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
```
- Servlet 은 HttpServlet 을 상속받아야 함
- RequestFacade 는 HttpServletRequest 에 대한 WAS 들의 구현체 중 하나. 지금은 Tomcat 을 쓰고 있으므로, RequestFacade 는 Tomcat 의 구현체
- ResponseFacade 는 HttpServletResponse 에 대한 WAS 들의 구현체 중 하나. 지금은 Tomcat 을 쓰고 있으므로, ResponseFacade 는 Tomcat 의 구현체

```java
String username = request.getParameter("username");
response.setContentType("text/plain"); // 보낼 데이터의 타입
response.setCharacterEncoding("utf-8"); // 인코딩 정보, 옛날 시스템이 아니라면 UTF-8 을 서야 함
response.getWriter().write("hello " + username); // body 에 담아서 보내줌
```

## 섹션2.3 로깅
```shell
logging.level.org.apache.coyote.http11=debug
```
- application.properties
- http1.1 의 내용을 로깅하겠다는 의미 
### 적용하기 전 (before) 
```shell
request = org.apache.catalina.connector.RequestFacade@9c92855
response = org.apache.catalina.connector.ResponseFacade@6fef9a36
```

### 적용 후 (after)
```shell
Host: localhost:8080
Connection: keep-alive
Cache-Control: max-age=0
sec-ch-ua: "Whale";v="3", "Not-A.Brand";v="8", "Chromium";v="118"
sec-ch-ua-mobile: ?0
sec-ch-ua-platform: "macOS"
Upgrade-Insecure-Requests: 1
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Whale/3.23.214.17 Safari/537.36
Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
Sec-Fetch-Site: none
Sec-Fetch-Mode: navigate
Sec-Fetch-User: ?1
Sec-Fetch-Dest: document
Accept-Encoding: gzip, deflate, br
Accept-Language: ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7

]
request = org.apache.catalina.connector.RequestFacade@73fefc15
response = org.apache.catalina.connector.ResponseFacade@1be14b67
```