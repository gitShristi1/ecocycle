<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - EcoCycle</title>
</head>
<body>
    <h1>Create your EcoCycle account</h1>

    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <p>Full name *<br>
           <input type="text" name="fullName" value="<c:out value='${param.fullName}'/>" required></p>
        <p>Email *<br>
           <input type="email" name="email" value="<c:out value='${param.email}'/>" required></p>
        <p>Password *<br>
           <input type="password" name="password" required></p>
        <p>Phone<br>
           <input type="text" name="phone" value="<c:out value='${param.phone}'/>"></p>
        <p>Address<br>
           <input type="text" name="address" value="<c:out value='${param.address}'/>"></p>
        <p>City<br>
           <input type="text" name="city" value="<c:out value='${param.city}'/>"></p>
        <p><button type="submit">Register</button></p>
    </form>

    <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
</body>
</html>