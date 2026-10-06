<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Orders"/>
</jsp:include>

<h1>My orders</h1>

<c:if test="${param.msg == 'placed'}">
    <div class="alert alert-success">Order <c:out value="#${param.id}"/> placed. Thank you!</div>
</c:if>

<c:if test="${empty orders}">
    <div class="panel">You have not placed any orders yet. <a href="${pageContext.request.contextPath}/user/store">Browse the store</a>.</div>
</c:if>

<c:if test="${not empty orders}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Order</th>
                <th>Date</th>
                <th>Items</th>
                <th>Total (&#8377;)</th>
                <th>Status</th>
                <th>Shipping address</th>
            </tr>
            <c:forEach items="${orders}" var="o">
                <tr>
                    <td>#<c:out value="${o.orderId}"/></td>
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