<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false"%>

<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Messenger</title>
        <link rel="stylesheet" type="text/css" href="stylesheets/style.css">
        <link rel="stylesheet" type="text/css" href="stylesheets/messages-style.css">
        <!-- for fonts only -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
        <link rel="icon" type="image/png" href="images/icon.png">
        <script src="scripts/auth.js"></script>
        <script src="scripts/scripts.js"></script>
        <script src="scripts/rendering.js"></script>
    </head>

    <script>
        Application.updAuthor = "<c:out value="${requestScope.author}" />";
        Application.updUid = "<c:out value="${requestScope.uid}" />";
        setAuthor();
        if (!userAuthorized()) {
            window.location = Application.rootUrl + "/login.jsp";
        }
    </script>

    <body onload="run()">
        <header>
            <div id="header-left">
                <span id="logo">Messenger</span>
                <a href="https://github.com/UladBohdan/practice-famcs-16"
                   target="_blank"
                   id="github-link" title="View source code on GitHub">
                    <i class="fa fa-github"></i>
                </a>
                <span id="connection"></span>
            </div>

            <div id="header-right">
                <span id="author"></span>
                <button id="log-out-button" onclick="logOut()">Log out</button>
            </div>
        </header>

        <div id="message-history"></div>

        <footer>
            <div id="send-button-container">
                <i class="fa fa-paper-plane" id="send-button" onclick="sendMessage()"></i>
            </div>
            <div id="input-text-container">
                <textarea id="input-text" name="new_message" title="New message" onkeydown = "if (event.keyCode == 13) sendMessage()" ></textarea>
            </div>
        </footer>

    </body>
</html>