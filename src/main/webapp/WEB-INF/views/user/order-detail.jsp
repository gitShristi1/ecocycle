<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Order Details"/>
</jsp:include>

<h1>Order #<c:out value="${order.orderId}"/></h1>

<div class="panel">
    <p><strong>Placed:</strong> <fmt:formatDate value="${order.createdAt}" pattern="dd MMM yyyy HH:mm"/></p>
    <p><strong>Status:</strong> <c:out value="${order.status}"/></p>
    <p><strong>Shipping address:</strong> <c:out value="${order.shippingAddress}"/></p>
</div>

<div class="table-wrap">
    <table class="table">
        <tr>
            <th>Product</th>
            <th>Sold by</th>
            <th>Price (&#8377;)</th>
            <th>Qty</th>
            <th>Subtotal (&#8377;)</th>
        </tr>
        <c:forEach items="${items}" var="item">
            <tr>
                <td><c:out value="${item.productName}"/></td>
                <td><c:out value="${item.companyName}"/></td>
                <td><fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><c:out value="${item.quantity}"/></td>
                <td><fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        <tr class="total-row">
            <td colspan="4">Total</td>
            <td>&#8377;<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2" maxFractionDigits="2"/></td>
        </tr>
    </table>
</div>

<p><a href="${pageContext.request.contextPath}/user/orders">&larr; Back to my orders</a></p>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>