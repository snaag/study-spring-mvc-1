# 섹션3. 서블릿, JSP, MVC 패턴 
## 3.1 회원가입 웹 애플리케이션 요구사항 안내 
- 기본 기능
  - 회원 조회 (Retrieve)
  - 회원 저장 (Create)
- 개발 순서 
  - core 모듈 -> 핵심 비즈니스 로직 
- 개선 순서  
  1. Servlet 으로 개발
  2. JSP 로 개발 
  3. MVC 패턴으로 개발

## 3.2 Member *C*.reate, *R*.etrieve 구현 

### Member class 생성 
- Lombok 의 @Getter, @Setter 를 사용하였으므로 Member class 의 getter, setter 를 별도로 만들지 않아도 됨  

```java
@Getter @Setter // Lombok 사용 
public class Member {
    private Long id;
    private String username;
    private int age;

    public Member() { }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

### MemberRepository (구현) 클래스 구현
- 동시성 문제로 실무에서는 HashMap 대신 ConcurrentHashMap, Long 대신 AtomicLong 을 사용함
- singleton 
  - singleton 으로 만들기 위해 `private 생성자` 로 MemberRepository 를 선언하였음 
  - 외부에서 사용할 때에는 미리 만들어둔 `instance` 를 사용하도록 `getInstance()` 함수를 구현해둠  

```java
/**
 * 동시성 문제로 실무에서는 HashMap -> ConcurrentHashMap, Long -> AtomicLong 을 사용함
 */
public class MemberRepository {

    // private 생성자라서 static 하지 않아도 되지만, 일단 해두도록 함
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    // singleton 으로 만듦
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance() {
        return instance;
    }

    // singleton 으로 만들 때에는 private 생성자를 만들어서, 외부에서 생성할 수 없도록 해야 함
    private MemberRepository() { }

    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id) {
        return store.get(id);
    }

    public List<Member> findAll() {
        // 굳이 새로 선언해서 주는 이유는, store 를 보호하기 위해서임 (불변성?)
        return new ArrayList<>(store.values());
    }

    public void clearStore() {
        store.clear();
    }
}
```

### MemberRepositoryTest 
- 테스트 작성 방법
  - `given` 이런게 주어졌을 때
  - `when` 이렇게 하였을 때
  - `then` 이런 결과가 나와야 해
- `@AfterEach`
  - 매 @Test 가 완료되면 수행되는 함수 

```java
class MemberRepositoryTest {

    // MemberRepository memberRepository = new MemberRepository(); // ! X ( private 생성자 이므로 안됨 )
    MemberRepository memberRepository = MemberRepository.getInstance();

    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    @Test
    void save() {
        // given (이런게 주어졌을 때)
        Member member = new Member("hello", 20);

        // when (이렇게 하였을 때)
        Member savedMember = memberRepository.save(member);

        // then (이런 결과가 나와야 해)
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void findAll() {
        // given (이런게 주어졌을 때)
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when (이렇게 하였을 때)
        List<Member> result = memberRepository.findAll();

        // then (이런 결과가 나와야 해)
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }
}
```

## 3.3 [Servlet] 불편하게 회원 정보에 대한 동적인 HTML 반환하기
- Servlet 을 사용하여 동적인 HTML 을 client 에게 줄 수 있음
- 그러나 Servlet 에서 HTML 을 작성하는 것은 너무 불편하다! -> **템플릿엔진 (JSP `next!`, Thymeleaf 등) 사용**  

### 회원 정보 저장
- 저장 Form 

<img width="608" alt="image" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/7638425b-6cdd-4cd7-a2d7-1d96aa09612b">

```java
@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.write("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Title</title>
                </head>
                <body>
                <form action="/servlet/members/save" method="post">
                    username: <input type="text" name="username" />
                    age:      <input type="text" name="age" />
                 <button type="submit">전송</button>
                </form>
                </body>
                </html>
                """);
        // ! servlet 으로 HTML 을 보내주려하면 상당히 불편하다

    }
}
```

- 저장 완료 페이지

<img width="463" alt="스크린샷 2024-01-02 오전 2 10 07" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/26f07e5b-ddb0-4d18-a2c1-13e8367852fa">

```java
@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {

    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get 의 Parameter, Post 의 Form data 이든 상관 없음
        // 무조건 문자로 받음
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                " <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                "    <li>id="+member.getId()+"</li>\n" + // 동적으로 response 전송 가능
                "    <li>username="+member.getUsername()+"</li>\n" + // 동적으로 response 전송 가능
                "    <li>age="+member.getAge()+"</li>\n" + // 동적으로 response 전송 가능
                "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" +
                "</body>\n" +
                "</html>");
    }
}
```

### 회원 정보 확인

<img width="379" alt="스크린샷 2024-01-02 오전 2 27 56" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/c8af8663-9de1-45e4-a944-34788b771fae">

```java
@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE htm>\n");
        writer.write("<html>\n");
        writer.write("<head>\n");
        writer.write("  <meta charset=\"utf-8\">\n");
        writer.write("  <title>Title</title>\n");
        writer.write("</head>\n");
        writer.write("<body>\n");
        writer.write("<a href=\"/index.html\">메인</a>\n");
        writer.write("<table>\n");
        writer.write("  <thead>\n");
        writer.write("  <th>id</th>\n");
        writer.write("  <th>username</th>\n");
        writer.write("  <th>age</th>\n");
        writer.write("  </thead>\n");
        writer.write("  <tbody>\n");

        for (Member member : members) {
            writer.write("    <tr>\n");
            writer.write("      <td>" + member.getId() + "\n");
            writer.write("      <td>" + member.getUsername() + "\n");
            writer.write("      <td>" + member.getAge() + "\n");
            writer.write("    </tr>\n");
        }

        writer.write("  </tbody>\n");
        writer.write("</table>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");
    }
}

```

## 3.4 [JSP] 덜 불편하게 회원 정보에 대한 동적인 HTML 반환하기

### 회원 정보 저장

- 저장 Form 

<img width="477" alt="스크린샷 2024-01-02 오전 2 49 58" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/a52eb5fd-8c33-4e30-8ae9-b3c0c3e675b5">

```jsp
<%-- JSP 라는 의미임 (이 줄이 꼭 있어야 됨) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="/jsp/members/save.jsp" method="post">
    username: <input type="text" name="username">
    age:      <input type="text" name="age">
    <button type="submit">전송</button>
</form>
</body>
</html>
```

- 저장 완료 페이지 

<img width="430" alt="스크린샷 2024-01-02 오전 2 55 23" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/00f483e4-f554-4839-a9f1-c1764e2eccb4">

```java
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- import --%>
<%@ page import="com.example.servlet.domain.member.MemberRepository" %>
<%@ page import="com.example.servlet.domain.member.Member" %>

<%-- MemberSaveServlet 에 있던 로직 --%>
<%
  System.out.println("save.jsp");

  MemberRepository memberRepository = MemberRepository.getInstance();
  // request, response 는 다른 import, 선언 없이 사용 가능
  String username = request.getParameter("username");
  int age = Integer.parseInt(request.getParameter("age"));

  Member member = new Member(username, age);
  memberRepository.save(member);
  System.out.println("member = " + member);
%>
<html>
<head>
  <title>Title</title>
</head>
<body>
성공
<ul>
  <li>id=<%= member.getId() %></li>
  <li>username=<%= member.getUsername() %></li>
  <li>age=<%= member.getAge() %></li>
</ul>
<a href="/index.html">메인</a>
</body>
</html>
```

### 회원 목록 확인

<img width="397" alt="스크린샷 2024-01-02 오전 2 58 34" src="https://github.com/snaag/study-spring-mvc-1/assets/42943992/27bb40f9-d2ba-452f-8acc-cedace56e821">

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.servlet.domain.member.MemberRepository" %>
<%@ page import="com.example.servlet.domain.member.Member" %>
<%@ page import="java.util.List" %>

<%-- MemberListServlet 에 있던 로직 --%>
<%
    MemberRepository memberRepository = MemberRepository.getInstance();
    List<Member> members = memberRepository.findAll();
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<a href="/index.html">메인</a>
<table>
    <thead>
    <th>id</th>
    <th>username</th>
    <th>age</th>
    </thead>
    <tbody>
    <%
        for (Member member : members) {
            out.write("<tr>\n");
            out.write("  <td>" + member.getId() + "</td>\n");
            out.write("  <td>" + member.getUsername() + "</td>\n");
            out.write("  <td>" + member.getAge() + "</td>\n");
            out.write("</tr>\n");
        }
    %>
    </tbody>
</table>
</body>
</html>
```

### JSP 사용 방법 
- 경로
  - webapp/jsp
  - `http://localhost:8080/jsp/xx.jsp`
- JSP 문법
  - JSP 사용 선언
    - `<%@ page contentType="text/html;charset=UTF-8" language="java" %>`
  - import
    - `<%@ page import="com.example.servlet.domain.member.MemberRepository" %>`
  - Java 코드 사용
    - `<% ... %>`

  
### 그럼에도 불편한... JSP 의 한계
- 코드가 지저분하고 복잡해짐 
  - 화면을 보여주는 부분 (view) 과 비즈니스 로직을 처리하는 부분이 한 페이지에 있어 **번거로움**
- 작업의 어려움 
  - 하나의 파일에 화면과 비즈니스 로직이 있으므로, 커밋 시 충돌도 잘 발생함
  - 유지보수도 어려워짐 

### 보다 개선된 MVC (Model, View, Controller) 패턴 
- 화면을 보여주는 로직, 비즈니스 로직을 분리하도록 함  
- 기존 프로젝트의 개선 방법 
  - Servlet 에서 비즈니스 로직을 돌리도록 함
  - JSP 에서 화면을 그리도록 함 
