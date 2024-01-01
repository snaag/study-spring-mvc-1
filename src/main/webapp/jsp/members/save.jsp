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