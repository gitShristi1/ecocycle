<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EcoCycle</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <a class="brand" href="${pageContext.request.contextPath}/">EcoCycle</a>
</header>
<main>
    <div class="form-card" style="text-align:center;">
        <c:choose>
            <c:when test="${code == 403}">
                <h1>Access denied</h1>
                <p>You do not have permission to open this page.</p>
            </c:when>
            <c:when test="${code == 404}">
                <h1>Page not found</h1>
                <p>The page you are looking for does not exist, or is no longer available.</p>
            </c:when>
            <c:when test="${code == 400 or code == 405}">
                <h1>Invalid request</h1>
                <p>That request could not be processed.</p>
            </c:when>
            <c:otherwise>
                <h1>Something went wrong</h1>
                <p>We could not complete that request. Nothing has been lost, so please try again in a moment.</p>
            </c:otherwise>
        </c:choose>
        <p><a class="btn btn-primary" href="${pageContext.request.contextPath}/">Back to the home page</a></p>
    </div>
</main>
</body>
</html>