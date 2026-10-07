<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Company Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.companyName}"/>!</h1>

<c:if test="${not empty summary}">
    <div class="stats">
        <div class="stat">
            <div class="num"><c:out value="${summary.units}"/></div>
            <div class="label">Units sold</div>
        </div>
        <div class="stat">
            <div class="num">&#8377;<fmt:formatNumber value="${summary.revenue}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Revenue</div>
        </div>
        <div class="stat">
            <div class="num">&#8377;<fmt:formatNumber value="${summary.net}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Net earnings</div>
        </div>
    </div>
    <p><a href="${pageContext.request.contextPath}/company/sales">See full sales details &rarr;</a></p>
</c:if>

<div class="panel">
    <p><a href="${pageContext.request.contextPath}/company/requests">Browse open requests</a>,
       see <a href="${pageContext.request.contextPath}/company/pickups">your pickups</a>,
       or manage <a href="${pageContext.request.contextPath}/company/products">your products</a>.</p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>