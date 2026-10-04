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

    <h2>Companies</h2>
    <p>
        <a href="${pageContext.request.contextPath}/admin/companies">Pending approvals</a>
        <c:if test="${not empty pendingCount}">(<c:out value="${pendingCount}"/>)</c:if>
    </p>

    <p>Pricing, monitoring and statistics are coming soon.</p>
    <p><a href="${pageContext.request.contextPath}/logout">Log out</a></p>
</body>
</html>