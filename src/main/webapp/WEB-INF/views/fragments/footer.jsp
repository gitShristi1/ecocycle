<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
</main>
<footer class="site-footer">
    <span>&copy; 2026 EcoCycle</span>
    <c:if test="${empty sessionScope.role}">
        <a href="${pageContext.request.contextPath}/admin-login">Admin Login</a>
    </c:if>
</footer>
</body>
</html>