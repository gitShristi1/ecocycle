<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Checkout"/>
</jsp:include>

<h1>Checkout</h1>

<div class="table-wrap">
    <table class="table">
        <tr>
            <th>Product</th>
            <th>Price (&#8377;)</th>
            <th>Qty</th>
            <th>Subtotal (&#8377;)</th>
        </tr>
        <c:forEach items="${lines}" var="line">
            <tr>
                <td><c:out value="${line.product.productName}"/></td>
                <td><fmt:formatNumber value="${line.product.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <td><c:out value="${line.quantity}"/></td>
                <td><fmt:formatNumber value="${line.lineTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            </tr>
        </c:forEach>
        <tr class="total-row">
            <td colspan="3">Total</td>
            <td>&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></td>
        </tr>
    </table>
</div>

<div class="form-card" style="margin-top:1.5rem;">
    <h2>Delivery details</h2>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="e">
                    <li><c:out value="${e}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/user/checkout"
          onsubmit="this.querySelector('button').disabled = true;">
        <label>Shipping address *
            <input type="text" name="shippingAddress" maxlength="300"
                   value="<c:out value='${shippingAddress}'/>" required>
        </label>
        <button type="submit" class="btn btn-primary btn-block">Place order</button>
    </form>
    <p class="form-note">Payments are simulated: placing an order records it in the system, and no real money moves.</p>
    <p class="form-note"><a href="${pageContext.request.contextPath}/user/cart">&larr; Back to cart</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>