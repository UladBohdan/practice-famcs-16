<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>

<html>
<head>
    <title>Login to Messenger</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/png" href="images/icon.png">
    <link rel="stylesheet" type="text/css" href="stylesheets/login-style.css">
</head>
<body class="login">
    <h1 class="title">Messenger</h1>
    <c:choose>
        <c:when test="${requestScope.errorMsg!=null}">
            <c class="error">
                <c:out value="${requestScope.errorMsg}"></c:out>
            <br>
            </c>
        </c:when>
        <c:otherwise></c:otherwise>
    </c:choose>
    <form action="/login" method="post" class="fields">
        <input name="username" placeholder="Username" class="input"><br>
        <input type="password" name="password" placeholder="Password" class="input"><br>
        <small>Use <strong>Guest</strong> account to log in (password: password)</small><br><br>
        <button type="submit" class="input">Log in</button>
    </form>
</body>

</html>