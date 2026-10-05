<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Requests"/>
</jsp:include>

<h1>My waste requests</h1>
<p><a class="btn btn-primary btn-small" href="${pageContext.request.contextPath}/user/sell">Sell more waste</a></p>

<c:if test="${param.msg == 'submitted'}">
    <div class="alert alert-success">Request submitted. A recycling company will pick it up soon.</div>
</c:if>

<c:if test="${empty requests}">
    <div class="panel">You have not submitted any requests yet.</div>
</c:if>

<c:if test="${not empty requests}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>#</th>
                <th>Waste</th>
                <th>Weight (kg)</th>
                <th>Rate (&#8377;/kg)</th>
                <th>Amount (&#8377;)</th>
                <th>Status</th>
                <th>Submitted</th>
            </tr>
            <c:forEach items="${requests}" var="r">
                <tr>
                    <td><c:out value="${r.requestId}"/></td>
                    <td><c:out value="${r.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${r.weightKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.ratePerKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${r.status}"/></td>
                    <td><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>