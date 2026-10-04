<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - EcoCycle</title>
</head>
<body>
    <h1>Log in to EcoCycle</h1>

    <c:if test="${param.registered == '1'}">
        <p style="color:green;">Account created. Please log in.</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <p>Email<br>
           <input type="email" name="email" value="<c:out value='${param.email}'/>" required></p>
        <p>Password<br>
           <input type="password" name="password" required></p>
        <p><button type="submit">Log in</button></p>
    </form>

    <p>New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
</body>
</html>