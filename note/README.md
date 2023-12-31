# 섹션2. 서블릿
## 2.1 Project 만들기

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

## 2.2 ServletComponentScan 
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

## 2.3 Servlet 만들기
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

## 2.4 로깅
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

## 2.5 HTTP 메시지 출력
### 메시지 출력 
```java
private static void printStartLine(HttpServletRequest request) {
    System.out.println("--- START LINE ---");
    System.out.println("request.getMethod() = " + request.getMethod()); // GET
    System.out.println("request.getProtocol() = " + request.getProtocol()); // HTTP/1.1
    System.out.println("request.getScheme() = " + request.getScheme()); // http
    System.out.println("request.getRequestURL() = " + request.getRequestURL()); // http://localhost:8080/request-header
    System.out.println("request.getRequestURI() = " + request.getRequestURI()); // /request-header
    System.out.println("request.getQueryString() = " + request.getQueryString()); // null
    System.out.println("request.isSecure() = " + request.isSecure()); // false (https 사용 유무)

    System.out.println("--- END LINE ---");
}
```
```shell
--- START LINE ---
request.getMethod() = GET
request.getProtocol() = HTTP/1.1
request.getScheme() = http
request.getRequestURL() = http://localhost:8080/request-header
request.getRequestURI() = /request-header
request.getQueryString() = name=jane
request.isSecure() = false
--- END LINE ---
```

### header 조회 (1)
```java
private static void printHeaders(HttpServletRequest request) {
    System.out.println("--- HEADERS START LINE ---");

    // 방법 1
    System.out.println("@방법 1");
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        System.out.println(headerName + ":" + request.getHeader(headerName));
    }

    // 방법 2
    System.out.println("@방법 2");
    request.getHeaderNames().asIterator()
            .forEachRemaining(headerName ->
                    System.out.println(headerName + ":" + request.getHeader(headerName))
            );


    System.out.println("--- HEADERS END LINE ---");
}
```

```shell
--- HEADERS START LINE ---
@방법 1
host:localhost:8080
connection:keep-alive
cache-control:max-age=0
sec-ch-ua:"Google Chrome";v="117", "Not;A=Brand";v="8", "Chromium";v="117"
sec-ch-ua-mobile:?0
sec-ch-ua-platform:"macOS"
upgrade-insecure-requests:1
user-agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36
accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
sec-fetch-site:none
sec-fetch-mode:navigate
sec-fetch-user:?1
sec-fetch-dest:document
accept-encoding:gzip, deflate, br
accept-language:ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
cookie:_ga=GA1.1.247559615.1693740907; _ga_ZF336Z6LWJ=GS1.1.1693740906.1.1.1693742918.0.0.0
@방법 2
host:localhost:8080
connection:keep-alive
cache-control:max-age=0
sec-ch-ua:"Google Chrome";v="117", "Not;A=Brand";v="8", "Chromium";v="117"
sec-ch-ua-mobile:?0
sec-ch-ua-platform:"macOS"
upgrade-insecure-requests:1
user-agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36
accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
sec-fetch-site:none
sec-fetch-mode:navigate
sec-fetch-user:?1
sec-fetch-dest:document
accept-encoding:gzip, deflate, br
accept-language:ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7
cookie:_ga=GA1.1.247559615.1693740907; _ga_ZF336Z6LWJ=GS1.1.1693740906.1.1.1693742918.0.0.0
--- HEADERS END LINE ---
```

### header 조회 (2)
```java
private void printHeaderUtils(HttpServletRequest request) {
    System.out.println("--- Header 편의 조회 start ---");
    System.out.println("[Host 편의 조회]");
    System.out.println("request.getServerName() = " + request.getServerName()); // Host 헤더
    System.out.println("request.getServerPort() = " + request.getServerPort()); // Host 헤더 System.out.println();

    System.out.println();
    System.out.println("[Accept-Language 편의 조회]");
    request.getLocales().asIterator()
            .forEachRemaining(locale ->
                    System.out.println("locale = " + locale));
    System.out.println("request.getLocale() = " + request.getLocale());
    System.out.println();

    System.out.println("[cookie 편의 조회]");
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            System.out.println(cookie.getName() + ": " + cookie.getValue());
        }
    }
    System.out.println();

    System.out.println("[Content 편의 조회]");
    System.out.println("request.getContentType() = " +
            request.getContentType());
    System.out.println("request.getContentLength() = " +
            request.getContentLength());
    System.out.println("request.getCharacterEncoding() = " +
            request.getCharacterEncoding());
    System.out.println("--- Header 편의 조회 end ---");
    System.out.println();
}
```
```shell
--- Header 편의 조회 start ---
[Host 편의 조회]
request.getServerName() = localhost
request.getServerPort() = 8080

[Accept-Language 편의 조회]
locale = ko_KR
locale = ko
locale = en_US
locale = en
request.getLocale() = ko_KR

[cookie 편의 조회]
_ga: GA1.1.247559615.1693740907
_ga_ZF336Z6LWJ: GS1.1.1693740906.1.1.1693742918.0.0.0

[Content 편의 조회]
request.getContentType() = null
request.getContentLength() = -1
request.getCharacterEncoding() = UTF-8
--- Header 편의 조회 end ---
```

### 기타 내용 조회 
```java
private void printEtc(HttpServletRequest request) {
    System.out.println("--- 기타 조회 start ---");

    System.out.println("[Remote 정보]");
    System.out.println("request.getRemoteHost() = " + request.getRemoteHost());
    System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr());
    System.out.println("request.getRemotePort() = " + request.getRemotePort());
    System.out.println();

    System.out.println("[Local 정보]");
    System.out.println("request.getLocalName() = " + request.getLocalName());
    System.out.println("request.getLocalAddr() = " + request.getLocalAddr());
    System.out.println("request.getLocalPort() = " + request.getLocalPort());

    System.out.println("--- 기타 조회 end ---");
    System.out.println();
}
```
```shell
--- 기타 조회 start ---
[Remote 정보]
request.getRemoteHost() = 0:0:0:0:0:0:0:1
request.getRemoteAddr() = 0:0:0:0:0:0:0:1
request.getRemotePort() = 50349

[Local 정보]
request.getLocalName() = localhost
request.getLocalAddr() = 0:0:0:0:0:0:0:1
request.getLocalPort() = 8080
--- 기타 조회 end ---
```

## 2.6 HTML 요청 데이터
- HTTP 요청 메시지를 통해 `클라이언트 -> 서버` 로 데이터를 전달하는 방법에는 **주로 3가지** 가 있다
- **HttpServletRequest 를 사용하여 쉽게 읽을 수 있음**

### 1. 쿼리 파라미터 (ex. GET)
- body 없이 쿼리 파라미터에 데이터 포함해서 전달
  - `getParameter()` 로 조회 가능
- ex. 검색, 필터, 페이징등에서 많이 사용하는 방식

```shell
# 요청 URL
http://localhost:8080/request-param?username=name1&username=name100
```

```java
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
```

```shell
[전체 쿼리 파라미터 조회] - start
@방법 1 - name 으로 조회
paramName = username:name1
@방법 2 - name 에 대응되는 값이 여러개인 경우
username = name1
username = name100
[전체 쿼리 파라미터 조회] - end
```

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body> <ul>
    <li><a href="basic.html">서블릿 basic</a></li> </ul>
</body>
</html>
```

<img width="470" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/06ad2e86-c14b-4536-8ed2-8d2596069992">

<img width="1007" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/9308ce38-f58b-4217-a477-a8be13587d0f">


### 2. HTML Form (ex. POST)
- `content-type: application/x-www-form-urlencoded`
- body 에 쿼리 파라미터 형식으로 전달
  - `getParameter()` 로 조회 가능 (1번과 동일)
- ex. 회원 가입, 상품 주문 등에서 HTML Form 의 형태로 사용
- (화면 예시는 1번과 동일)

### 3. HTTP message body 에 데이터를 직접 담아서 요청 
- HTTP API 에서 주로 사용 
- 데이터 형식으로는 주로 JSON 사용 (JSON, XML, TEXT 등) 
  - 과거에는 XML 을 주로 사용했으나, **최근에는 JSON** 이 표준이 됨

```java
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
```

<img width="1004" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/c10a4c09-ade6-4347-b24d-d36fe9f71a2d">

## 2.8 JSON 으로 데이터 주고받기
- 파싱을 위한 class 선언 필요

```java
@Getter @Setter
public class HelloData {
    private String username;
    private int age;
}
```

- Jackson 의 ObjectMapper 로 파싱
```java
// import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

    // * Jackson -> SpringBoot 에서 제공하는 Json library
    // * cf. Gson

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        System.out.println("messageBody = " + messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);

        System.out.println("helloData.username = " + helloData.getUsername());
        System.out.println("helloData.age = " + helloData.getAge());

        response.getWriter().write("ok");
    }
}
```

## 2.9 Header 보내기 
```java
@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [status-line]
        response.setStatus(HttpServletResponse.SC_OK); // 200

        // [response-headers]
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // cache 무효화
        response.setHeader("Pragma","no-cache"); // 과거 버전의 캐시도 없앤다
        response.setHeader("my-header", "hello"); // 나의 커스텀 헤더

        // [Header 편의 메서드]
//        content(response);
//        cookie(response);
        redirect(response);

        // [message body]
        PrintWriter writer = response.getWriter();
        writer.write("안녕");
    }

    private void content(HttpServletResponse response) {
        // Content-Type: text/plain;charset=utf-8
        // Content-Length: 2

        // response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
    }

    private void cookie(HttpServletResponse response) {
        // response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); // 600초 (이 cookie 는 600초 동안 유효하다)

        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        // Status Code 302
        // Location: /basic/hello-form.html

        // response.setStatus(HttpServletResponse.SC_FOUND); // 302
        // response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }
}
```

- header 에 값을 set 할 떄 (`response.setHeader("xx","yy");`)
  - `response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");`
  - `response.setHeader("Pragma","no-cache");`
  - `response.setHeader("my-header", "hello");`
- 특정 header 를 set 할 때 
  - `리다이렉트` `response.sendRedirect("/basic/hello-form.html");`
  - `content type` `response.setContentType("text/plain");`
  - `content type` `response.setCharacterEncoding("utf-8");`
- cookie 를 set 할 때
  - `cookie.setMaxAge(600);`
  - `response.addCookie(cookie);`

<img width="900" alt="스크린샷 2023-12-29 오후 7 50 41" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/f489d2bb-c715-4903-ba3f-7df8325f88ec">

## 2.10 서블릿으로 HTML response 보내기
- header 
  - contentType 을 text/html 로 지정해야 함
  - 한글로 보내려면 charset 도 utf-8 로 해주어야 함 
- body
  - writer.println("<html>"); 으로 보낼 수 있음 

```java
@WebServlet(name="responseHtmlServlet", urlPatterns = "/response-html")
public class ResponseHtmlServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: text/html;charset=utf-8;
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<body>");
        writer.println("  <div>안녕</div>");
        writer.println("</body>");
        writer.println("</html>");
    }
}
```
<img width="900" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/732fb9ce-2dbd-4406-9526-225d2ce28e5b">

## 2.11 서블릿으로 JSON response 보내기

- header
  - contentType 을 application/json 으로 지정해야 함
    - **application/json 은 스펙상 utf-8 형식을 지원하도록 되어있어, charset=utf-8 같은 추가 파라미터 지원 X**
- body
  - ObjectMapper 를 사용하여 string 으로 변환해서 보내기 

```java
@WebServlet(name="responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Content-Type: application/json; charset=utf-8

        response.setContentType("application/json");
        // response.setCharacterEncoding("utf-8"); // 안써도 됨 

        // 1. 보낼 데이터 준비
        HelloData helloData = new HelloData();
        helloData.setUsername("kim");
        helloData.setAge(20);

        // 2. string 으로 보내기 (Jackson 의 ObjectMapper)
        // {"username": "kim", "age": 20}
        String result = objectMapper.writeValueAsString(helloData);
        response.getWriter().write(result);
    }
}
```

<img width="900" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/c4a7acbd-6a4b-4e64-9321-7335c9a9cd44">

### (추가) 만약 Spring MVC 를 사용한다면?
```java
HelloData helloData = new HelloData();
helloData.setUsername("kim");
return helloData.setAge(20);
```

**이렇게만 해줘도 된다!**

