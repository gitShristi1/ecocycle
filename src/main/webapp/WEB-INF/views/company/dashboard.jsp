<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Company Dashboard - EcoCycle</title>
</head>
<body>
    <h1>Welcome, <c:out value="${sessionScope.companyName}"/>!</h1>
    <p>This is your company dashboard. Waste requests and product management are coming soon.</p>
    <p><a href="${pageContext.request.contextPath}/logout">Log out</a></p>
</body>
</html>