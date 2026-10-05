<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${param.title}"/> - EcoCycle</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <a class="brand" href="${pageContext.request.contextPath}/">EcoCycle</a>
    <nav class="nav">
        <c:choose>
            <c:when test="${sessionScope.role == 'USER'}">
                <a href="${pageContext.request.contextPath}/user/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/user/sell">Sell Waste</a>
                <a href="${pageContext.request.contextPath}/user/requests">My Requests</a>
                <a href="${pageContext.request.contextPath}/user/transactions">Payments</a>
                <span class="nav-user"><c:out value="${sessionScope.userName}"/></span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </c:when>
            <c:when test="${sessionScope.role == 'COMPANY'}">
                <a href="${pageContext.request.contextPath}/company/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/company/requests">Open Requests</a>
                <a href="${pageContext.request.contextPath}/company/pickups">My Pickups</a>
                <span class="nav-user"><c:out value="${sessionScope.companyName}"/></span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </c:when>
            <c:when test="${sessionScope.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/companies">Companies</a>
                <a href="${pageContext.request.contextPath}/admin/pricing">Pricing</a>
                <span class="nav-user"><c:out value="${sessionScope.adminName}"/></span>
                <a href="${pageContext.request.contextPath}/logout">Log out</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">User Login</a>
                <a href="${pageContext.request.contextPath}/company-login">Company Login</a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<main>