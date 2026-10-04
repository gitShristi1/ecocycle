<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Company Login - EcoCycle</title>
</head>
<body>
    <h1>Company Login</h1>

    <c:if test="${param.registered == '1'}">
        <p style="color:green;">Registration submitted. You can log in once the admin approves your company.</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/company-login">
        <p>Email<br>
           <input type="email" name="email" value="<c:out value='${param.email}'/>" required></p>
        <p>Password<br>
           <input type="password" name="password" required></p>
        <p><button type="submit">Log in</button></p>
    </form>

    <p>Not registered yet? <a href="${pageContext.request.contextPath}/register-company">Register your company</a></p>
    <p><a href="${pageContext.request.contextPath}/login">Individual user? Log in here</a> |
       <a href="${pageContext.request.contextPath}/">Home</a></p>
</body>
</html>