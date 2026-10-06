<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Cart"/>
</jsp:include>

<h1>Your cart</h1>

<c:choose>
    <c:when test="${param.msg == 'added'}"><div class="alert alert-success">Added to your cart.</div></c:when>
    <c:when test="${param.msg == 'updated'}"><div class="alert alert-success">Cart updated.</div></c:when>
    <c:when test="${param.msg == 'removed'}"><div class="alert alert-success">Item removed.</div></c:when>
    <c:when test="${param.msg == 'limited'}"><div class="alert alert-error">The quantity was limited to the stock available.</div></c:when>
    <c:when test="${param.msg == 'outofstock'}"><div class="alert alert-error">That product is out of stock.</div></c:when>
    <c:when test="${param.msg == 'unavailable'}"><div class="alert alert-error">That product is no longer available.</div></c:when>
    <c:when test="${param.msg == 'cartfull'}"><div class="alert alert-error">Your cart is full (50 different products at most).</div></c:when>
    <c:when test="${param.msg == 'invalid'}"><div class="alert alert-error">That change was not valid.</div></c:when>
    <c:when test="${param.msg == 'review'}"><div class="alert alert-error">Please review your cart before checking out.</div></c:when>
</c:choose>
<c:if test="${not empty cartError}">
    <div class="alert alert-error"><c:out value="${cartError}"/> Nothing was ordered.</div>
</c:if>
<c:if test="${removedCount > 0}">
    <div class="alert alert-error">Some items were removed from your cart because they are no longer available.</div>
</c:if>

<c:if test="${empty lines}">
    <div class="panel">Your cart is empty. <a href="${pageContext.request.contextPath}/user/store">Browse the store</a>.</div>
</c:if>

<c:if test="${not empty lines}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Product</th>
                <th>Price (&#8377;)</th>
                <th>Quantity</th>
                <th>Subtotal (&#8377;)</th>
                <th></th>
            </tr>
            <c:forEach items="${lines}" var="line">
                <tr>
                    <td>
                        <c:out value="${line.product.productName}"/><br>
                        <span class="muted">by <c:out value="${line.product.companyName}"/></span>
                        <c:if test="${line.exceedsStock}">
                            <br><span class="stock-out">
                                <c:choose>
                                    <c:when test="${line.product.stock == 0}">Out of stock</c:when>
                                    <c:otherwise>Only <c:out value="${line.product.stock}"/> left</c:otherwise>
                                </c:choose>
                            </span>
                        </c:if>
                    </td>
                    <td><fmt:formatNumber value="${line.product.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/user/cart" class="qty-form">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="productId" value="${line.product.productId}">
                            <input type="number" name="quantity" value="${line.quantity}" min="1" max="99">
                            <button type="submit" class="btn btn-primary btn-small">Update</button>
                        </form>
                    </td>
                    <td><fmt:formatNumber value="${line.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/user/cart">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="productId" value="${line.product.productId}">
                            <button type="submit" class="btn btn-danger btn-small">Remove</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <tr class="total-row">
                <td colspan="3">Total</td>
                <td colspan="2">&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </table>
    </div>

    <c:if test="${blocked}">
        <div class="alert alert-error">Some items ask for more than is in stock. Adjust the quantities to continue.</div>
    </c:if>
    <p><a href="${pageContext.request.contextPath}/user/store">&larr; Continue shopping</a></p>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>