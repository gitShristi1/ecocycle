<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Company Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.companyName}"/>!</h1>
<div class="panel">
    <p><a href="${pageContext.request.contextPath}/company/requests">Browse open requests</a>,
       see <a href="${pageContext.request.contextPath}/company/pickups">your pickups</a>,
       or manage <a href="${pageContext.request.contextPath}/company/products">your products</a>.</p>
    <p class="muted">The sales dashboard is coming soon.</p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>