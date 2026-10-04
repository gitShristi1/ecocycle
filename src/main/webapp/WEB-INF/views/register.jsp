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

    <c:if test="${not empty errors}">
        <ul style="color:red;">
            <c:forEach items="${errors}" var="e">
                <li><c:out value="${e}"/></li>
            </c:forEach>
        </ul>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <p>Full name *<br>
           <input type="text" name="fullName" maxlength="100"
                  value="<c:out value='${param.fullName}'/>" required></p>
        <p>Email *<br>
           <input type="email" name="email" maxlength="150"
                  value="<c:out value='${param.email}'/>" required></p>
        <p>Password * <small>(8+ characters, at least one letter and one digit)</small><br>
           <input type="password" name="password" minlength="8" maxlength="72" required></p>
        <p>Confirm password *<br>
           <input type="password" name="confirmPassword" minlength="8" maxlength="72" required></p>
        <p>Phone <small>(10 digits)</small><br>
           <input type="text" name="phone" maxlength="10" pattern="[0-9]{10}"
                  value="<c:out value='${param.phone}'/>"></p>
        <p>Address<br>
           <input type="text" name="address" maxlength="300"
                  value="<c:out value='${param.address}'/>"></p>
        <p>City<br>
           <input type="text" name="city" maxlength="80"
                  value="<c:out value='${param.city}'/>"></p>
        <p><button type="submit">Register</button></p>
    </form>

    <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
</body>
</html>