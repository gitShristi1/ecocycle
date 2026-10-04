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
    <c:if test="${param.registered == 'company'}">
        <p style="color:green;">Registration submitted. You can log in once the admin approves your company.</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color:red;"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <p>Log in as<br>
           <select name="role">
               <option value="USER" ${param.role == 'COMPANY' ? '' : 'selected'}>User</option>
               <option value="COMPANY" ${param.role == 'COMPANY' ? 'selected' : ''}>Company</option>
           </select></p>
        <p>Email<br>
           <input type="email" name="email" value="<c:out value='${param.email}'/>" required></p>
        <p>Password<br>
           <input type="password" name="password" required></p>
        <p><button type="submit">Log in</button></p>
    </form>

    <p>New here?
       <a href="${pageContext.request.contextPath}/register">Create a user account</a> |
       <a href="${pageContext.request.contextPath}/register-company">Register a company</a></p>
</body>
</html>