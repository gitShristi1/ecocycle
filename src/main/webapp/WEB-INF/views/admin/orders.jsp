<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Monitoring: Orders"/>
</jsp:include>

<h1>Monitoring</h1>
<jsp:include page="/WEB-INF/views/fragments/admin-tabs.jsp">
    <jsp:param name="active" value="orders"/>
</jsp:include>

<c:if test="${empty orders}">
    <div class="panel">No orders have been placed yet.</div>
</c:if>

<c:if test="${not empty orders}">
    <c:if test="${orders.size() >= 200}"><p class="muted">Showing the 200 most recent orders.</p></c:if>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Order</th>
                <th>Customer</th>
                <th>Date</th>
                <th>Items</th>
                <th>Total (&#8377;)</th>
                <th>Status</th>
                <th>Shipping address</th>
            </tr>
            <c:forEach items="${orders}" var="o">
                <tr>
                    <td>#<c:out value="${o.orderId}"/></td>
                    <td><c:out value="${o.userName}"/></td>
                    <td><fmt:formatDate value="${o.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td><c:out value="${o.itemCount}"/></td>
                    <td><fmt:formatNumber value="${o.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${o.status}"/></td>
                    <td><c:out value="${o.shippingAddress}"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>