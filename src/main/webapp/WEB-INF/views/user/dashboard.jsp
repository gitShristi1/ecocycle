<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.userName}"/>!</h1>
<div class="panel">
    <div class="panel">
    <p><a href="${pageContext.request.contextPath}/user/sell">Sell waste</a> to a recycling company,
       check <a href="${pageContext.request.contextPath}/user/requests">your requests</a>,
       or see <a href="${pageContext.request.contextPath}/user/transactions">your payments</a>.</p>
    <p class="muted">The recycled product store is coming soon.</p>
</div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>