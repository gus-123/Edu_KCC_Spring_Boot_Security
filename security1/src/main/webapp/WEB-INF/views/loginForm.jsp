<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
    <h1>로그인 홈페이지</h1>
    <hr>
    <!-- action="/login" & name="username" & name="password" 무조건 이걸로 설정 -->
    <!-- CSRF를 위해 '<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>' 이 코드 무조건 필요! -->
    <form action="/login" method="post">
        <input type="text" name="username" placeholder="username"><br/>
        <input type="password" name="password" placeholder="password"><br/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><br/>
        <button>로그인</button>
        <a href="/joinForm">회원가입</a>
    </form>
</body>
</html>