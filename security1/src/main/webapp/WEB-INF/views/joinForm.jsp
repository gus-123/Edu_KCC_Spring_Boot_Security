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
    <h1>회원가입폼</h1>
    <hr>
    <form action="/join" method="post">  <!-- Spring Security에 동작 위임 -->
        <input type="text" name="username" placeholder="username"><br/>
        <input type="password" name="password" placeholder="password"><br/>
        <input type="text" name="email" placeholder="email"><br/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/><br/>
        <button>회원가입</button>
    </form>
</body>
</html>