<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - EcoCycle</title>
</head>
<body>
    <h1>Welcome, <c:out value="${sessionScope.adminName}"/>!</h1>
    <p>Company approvals and platform controls are coming next.</p>
    <p><a href="${pageContext.request.contextPath}/logout">Log out</a></p>
</body>
</html>