<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - EcoCycle</title>
</head>
<body>
    <h1>Welcome, <c:out value="${sessionScope.userName}"/>!</h1>
    <p>This is your EcoCycle dashboard. Waste requests and the product store are coming soon.</p>
    <p><a href="${pageContext.request.contextPath}/logout">Log out</a></p>
</body>
</html>