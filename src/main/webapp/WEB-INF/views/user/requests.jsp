<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Requests"/>
</jsp:include>

<h1>My waste requests</h1>
<p><a class="btn btn-primary btn-small" href="${pageContext.request.contextPath}/user/sell">Sell more waste</a></p>

<c:choose>
    <c:when test="${param.msg == 'submitted'}">
        <div class="alert alert-success">Request submitted. A recycling company will pick it up soon.</div>
    </c:when>
    <c:when test="${param.msg == 'cancelled'}">
        <div class="alert alert-success">Request cancelled.</div>
    </c:when>
    <c:when test="${param.msg == 'cannotcancel'}">
        <div class="alert alert-error">That request can no longer be cancelled.</div>
    </c:when>
</c:choose>

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
                <th>Company</th>
                <th>Submitted</th>
                <th></th>
            </tr>
            <c:forEach items="${requests}" var="r">
                <tr>
                    <td><c:out value="${r.requestId}"/></td>
                    <td><c:out value="${r.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${r.weightKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.ratePerKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${r.status}"/></td>
                    <td><c:out value="${r.companyName}"/></td>
                    <td><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td>
                        <c:if test="${r.status == 'SUBMITTED'}">
                            <form method="post" action="${pageContext.request.contextPath}/user/requests"
                                  onsubmit="return confirm('Cancel this request?');">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="requestId" value="${r.requestId}">
                                <button type="submit" class="btn btn-danger btn-small">Cancel</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>