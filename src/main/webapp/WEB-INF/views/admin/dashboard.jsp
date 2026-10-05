<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Admin Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.adminName}"/>!</h1>

<div class="panel">
    <h2>Companies</h2>
    <p>
        <a href="${pageContext.request.contextPath}/admin/companies">Pending approvals</a>
        <c:if test="${not empty pendingCount and pendingCount > 0}">
            <span class="badge"><c:out value="${pendingCount}"/></span>
        </c:if>
    </p>
</div>

<p class="muted">Pricing, monitoring and statistics are coming soon.</p>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>