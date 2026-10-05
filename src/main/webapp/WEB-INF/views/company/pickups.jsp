<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Pickups"/>
</jsp:include>

<h1>My pickups</h1>
<p class="muted">Payments are simulated: recording one saves it in the system, no real money moves.</p>

<c:choose>
    <c:when test="${param.msg == 'pickedup'}">
        <div class="alert alert-success">Marked as picked up. You can now record the payment.</div>
    </c:when>
    <c:when test="${param.msg == 'paid'}">
        <div class="alert alert-success">Payment recorded.</div>
    </c:when>
    <c:when test="${param.msg == 'invalid'}">
        <div class="alert alert-error">That request could not be updated. It may already have been handled.</div>
    </c:when>
    <c:when test="${param.msg == 'error'}">
        <div class="alert alert-error">Something went wrong and nothing was changed. Please try again.</div>
    </c:when>
</c:choose>

<c:if test="${empty requests}">
    <div class="panel">
        You have not accepted any requests yet.
        <a href="${pageContext.request.contextPath}/company/requests">Browse open requests</a>.
    </div>
</c:if>

<c:if test="${not empty requests}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>#</th>
                <th>Customer</th>
                <th>Phone</th>
                <th>Pickup address</th>
                <th>Waste</th>
                <th>Weight (kg)</th>
                <th>You pay (&#8377;)</th>
                <th>Status</th>
                <th></th>
            </tr>
            <c:forEach items="${requests}" var="r">
                <tr>
                    <td><c:out value="${r.requestId}"/></td>
                    <td><c:out value="${r.userName}"/></td>
                    <td><c:out value="${r.userPhone}"/></td>
                    <td><c:out value="${r.pickupAddress}"/>, <c:out value="${r.city}"/></td>
                    <td><c:out value="${r.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${r.weightKg}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${r.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${r.status}"/></td>
                    <td>
                        <c:if test="${r.status == 'ACCEPTED'}">
                            <form method="post" action="${pageContext.request.contextPath}/company/pickups"
                                  onsubmit="return confirm('Mark this request as picked up?');">
                                <input type="hidden" name="action" value="pickedup">
                                <input type="hidden" name="requestId" value="${r.requestId}">
                                <button type="submit" class="btn btn-primary btn-small">Mark picked up</button>
                            </form>
                        </c:if>
                        <c:if test="${r.status == 'PICKED_UP'}">
                            <form method="post" action="${pageContext.request.contextPath}/company/pickups"
                                  onsubmit="return confirm('Record a payment of &#8377;${r.totalAmount} to the customer?');">
                                <input type="hidden" name="action" value="pay">
                                <input type="hidden" name="requestId" value="${r.requestId}">
                                <button type="submit" class="btn btn-primary btn-small">Record payment</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>