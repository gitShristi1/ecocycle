<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Sales"/>
</jsp:include>

<h1>Sales</h1>
<p class="muted">Commission is the platform's share, calculated when each order was placed.</p>

<div class="stats">
    <div class="stat">
        <div class="num"><c:out value="${summary.units}"/></div>
        <div class="label">Units sold</div>
    </div>
    <div class="stat">
        <div class="num">&#8377;<fmt:formatNumber value="${summary.revenue}" minFractionDigits="2" maxFractionDigits="2"/></div>
        <div class="label">Revenue</div>
    </div>
    <div class="stat">
        <div class="num">&#8377;<fmt:formatNumber value="${summary.commission}" minFractionDigits="2" maxFractionDigits="2"/></div>
        <div class="label">Platform commission</div>
    </div>
    <div class="stat">
        <div class="num">&#8377;<fmt:formatNumber value="${summary.net}" minFractionDigits="2" maxFractionDigits="2"/></div>
        <div class="label">Net earnings</div>
    </div>
</div>

<c:if test="${empty byProduct}">
    <div class="panel">No sales yet. When customers buy your products, they will appear here.</div>
</c:if>

<c:if test="${not empty byProduct}">
    <h2>By product</h2>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Product</th>
                <th>Units</th>
                <th>Revenue (&#8377;)</th>
                <th>Commission (&#8377;)</th>
                <th>Net (&#8377;)</th>
            </tr>
            <c:forEach items="${byProduct}" var="row">
                <tr>
                    <td><c:out value="${row.productName}"/></td>
                    <td><c:out value="${row.units}"/></td>
                    <td><fmt:formatNumber value="${row.revenue}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${row.commission}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${row.net}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>

    <h2>Recent sales</h2>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Order</th>
                <th>Date</th>
                <th>Product</th>
                <th>Qty</th>
                <th>Amount (&#8377;)</th>
                <th>Net (&#8377;)</th>
            </tr>
            <c:forEach items="${recent}" var="s">
                <tr>
                    <td>#<c:out value="${s.orderId}"/></td>
                    <td><fmt:formatDate value="${s.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td><c:out value="${s.productName}"/></td>
                    <td><c:out value="${s.quantity}"/></td>
                    <td><fmt:formatNumber value="${s.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatNumber value="${s.net}" minFractionDigits="2" maxFractionDigits="2"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>