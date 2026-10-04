<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register your company - EcoCycle</title>
</head>
<body>
    <h1>Register your recycling company</h1>
    <p>New companies must be approved by the admin before they can log in.</p>

    <c:if test="${not empty errors}">
        <ul style="color:red;">
            <c:forEach items="${errors}" var="e">
                <li><c:out value="${e}"/></li>
            </c:forEach>
        </ul>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register-company">
        <p>Company name *<br>
           <input type="text" name="companyName" maxlength="150"
                  value="<c:out value='${param.companyName}'/>" required></p>
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
        <p><button type="submit">Submit registration</button></p>
    </form>

    <p>Already registered? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
</body>
</html>