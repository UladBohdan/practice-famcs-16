<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>

<!DOCTYPE html>
<html>
<head>
    <title>Login to Messenger</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image/png" href="images/icon.png">
    <link rel="stylesheet" type="text/css" href="<c:url value="stylesheets/login-style.css" />">
    <script src="scripts/auth.js"></script>
</head>

<script>
    if (userAuthorized()) {
        window.location = Application.rootUrl + "/homepage.jsp";
    }
</script>

<body class="login">
    <h1 class="title">Messenger</h1>
    <c:choose>
        <c:when test="${requestScope.errorMsg!=null}">
            <c class="error">
                <c:out value="${requestScope.errorMsg}" />
            <br>
            </c>
        </c:when>
        <c:otherwise />
    </c:choose>
    <form action="/login" method="post" class="fields">
        <input name="username" placeholder="Username" class="input"><br>
        <input type="password" name="password" placeholder="Password" class="input"><br>
        <p class="label">Use <strong>Guest</strong> account to log in (password: password)</p>
        <button type="submit" class="input">Log in</button>
    </form>
</body>

</html>