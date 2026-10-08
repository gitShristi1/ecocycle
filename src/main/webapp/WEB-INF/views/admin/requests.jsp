<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Monitoring: Waste Requests"/>
</jsp:include>

<h1>Monitoring</h1>
<jsp:include page="/WEB-INF/views/fragments/admin-tabs.jsp">
    <jsp:param name="active" value="requests"/>
</jsp:include>

<div class="tabs">
    <c:forEach items="${['ALL','SUBMITTED','ACCEPTED','PICKED_UP','PAID','CANCELLED']}" var="s">
        <a class="tab ${filter == s ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/requests?status=${s}"><c:out value="${s}"/></a>
    </c:forEach>
</div>

<c:if test="${empty requests}">
    <div class="panel">No waste requests here.</div>
</c:if>

<c:if test="${not empty requests}">
    <c:if test="${requests.size() >= 200}"><p class="muted">Showing the 200 most recent requests.</p></c:if>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>#</th>
                <th>Customer</th>
                <th>Company</th>
                <th>Waste</th>
                <th>Weight (kg)</th>
                <th>Amount (&#8377;)</th>
                <th>City</th>
                <th>Status</th>
                <th>Submitted</th>
            </tr>
            <c:forEach items="${requests}" var="r">
                <tr>
                    <td><c:out value="${r.requestId}"/></td>
                    <td><c:out value="${r.userName}"/></td>
                    <td><c:out value="${r.companyName}" default="-"/></td>
                    <td><c:out value="${r.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${r.weightKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${r.city}"/></td>
                    <td><c:out value="${r.status}"/></td>
                    <td><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>