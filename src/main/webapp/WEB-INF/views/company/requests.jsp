<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Open Requests"/>
</jsp:include>

<h1>Open waste requests</h1>
<p class="muted">The customer's address and phone are shared once you accept a request.</p>

<c:choose>
    <c:when test="${param.msg == 'accepted'}">
        <div class="alert alert-success">Request accepted.</div>
    </c:when>
    <c:when test="${param.msg == 'taken'}">
        <div class="alert alert-error">Sorry, that request was just taken by another company, or is no longer available.</div>
    </c:when>
</c:choose>

<div class="panel">
    <form method="get" action="${pageContext.request.contextPath}/company/requests"
          style="display:flex; gap:0.8rem; flex-wrap:wrap; align-items:flex-end;">
        <label>Waste type<br>
            <select name="typeId">
                <option value="">Any</option>
                <c:forEach items="${wasteTypes}" var="t">
                    <option value="${t.wasteTypeId}" ${param.typeId == t.wasteTypeId ? 'selected' : ''}>
                        <c:out value="${t.typeName}"/>
                    </option>
                </c:forEach>
            </select>
        </label>
        <label>City<br>
            <input type="text" name="city" maxlength="80" value="<c:out value='${param.city}'/>">
        </label>
        <button type="submit" class="btn btn-primary btn-small">Filter</button>
        <a href="${pageContext.request.contextPath}/company/requests">Clear</a>
    </form>
</div>

<c:if test="${empty requests}">
    <div class="panel">No open requests match right now.</div>
</c:if>

<c:if test="${not empty requests}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>#</th>
                <th>Waste</th>
                <th>Weight (kg)</th>
                <th>You pay (&#8377;)</th>
                <th>City</th>
                <th>Notes</th>
                <th>Submitted</th>
                <th></th>
            </tr>
            <c:forEach items="${requests}" var="r">
                <tr>
                    <td><c:out value="${r.requestId}"/></td>
                    <td><c:out value="${r.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${r.weightKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${r.city}"/></td>
                    <td><c:out value="${r.notes}"/></td>
                    <td><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/company/requests">
                            <input type="hidden" name="action" value="accept">
                            <input type="hidden" name="requestId" value="${r.requestId}">
                            <button type="submit" class="btn btn-primary btn-small">Accept</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>