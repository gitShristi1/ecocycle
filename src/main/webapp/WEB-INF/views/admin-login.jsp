<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Login - EcoCycle</title>
</head>
<body>
    <h1>Admin Login</h1>

    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin-login">
        <p>Email<br>
           <input type="email" name="email" value="<c:out value='${param.email}'/>" required></p>
        <p>Password<br>
           <input type="password" name="password" required></p>
        <p><button type="submit">Log in</button></p>
    </form>

    <p><a href="${pageContext.request.contextPath}/">Home</a></p>
</body>
</html>