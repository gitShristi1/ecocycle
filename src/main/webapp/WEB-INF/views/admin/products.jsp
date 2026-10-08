<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Monitoring: Products"/>
</jsp:include>

<h1>Monitoring</h1>
<jsp:include page="/WEB-INF/views/fragments/admin-tabs.jsp">
    <jsp:param name="active" value="products"/>
</jsp:include>

<c:choose>
    <c:when test="${param.msg == 'removed'}"><div class="alert alert-success">Product removed from the store.</div></c:when>
    <c:when test="${param.msg == 'stale'}"><div class="alert alert-error">That product had already been removed. The list has been refreshed.</div></c:when>
</c:choose>

<div class="panel">
    <form method="get" action="${pageContext.request.contextPath}/admin/products" class="filters">
        <input type="hidden" name="status" value="${filter}">
        <label>Search by product or company<br>
            <input type="text" name="q" maxlength="100" value="<c:out value='${param.q}'/>">
        </label>
        <button type="submit" class="btn btn-primary btn-small">Search</button>
        <a href="${pageContext.request.contextPath}/admin/products?status=${filter}">Clear</a>
    </form>
</div>

<div class="tabs">
    <c:forEach items="${['ACTIVE','REMOVED','ALL']}" var="s">
        <a class="tab ${filter == s ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/products?status=${s}"><c:out value="${s}"/></a>
    </c:forEach>
</div>

<c:if test="${empty products}">
    <div class="panel">No products found.</div>
</c:if>

<c:if test="${not empty products}">
    <c:if test="${products.size() >= 200}"><p class="muted">Showing the 200 most recent products.</p></c:if>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th></th>
                <th>Product</th>
                <th>Company</th>
                <th>Price (&#8377;)</th>
                <th>Stock</th>
                <th>Status</th>
                <th>Added</th>
                <th></th>
            </tr>
            <c:forEach items="${products}" var="p">
                <tr>
                    <td>
                        <c:choose>
                            <c:when test="${not empty p.imagePath}">
                                <img class="thumb"
                                     src="${pageContext.request.contextPath}/product-image?f=<c:out value='${p.imagePath}'/>"
                                     alt="">
                            </c:when>
                            <c:otherwise><div class="thumb thumb-empty"></div></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:out value="${p.productName}"/>
                        <c:if test="${not empty p.description}">
                            <div class="muted"><c:out value="${p.description}"/></div>
                        </c:if>
                    </td>
                    <td><c:out value="${p.companyName}"/></td>
                    <td><fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><c:out value="${p.stock}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.status == 'REMOVED'}"><span class="stock-out">REMOVED</span></c:when>
                            <c:otherwise><c:out value="${p.status}"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatDate value="${p.createdAt}" pattern="dd MMM yyyy"/></td>
                    <td>
                        <c:if test="${p.status == 'ACTIVE'}">
                            <form method="post" action="${pageContext.request.contextPath}/admin/products"
                                  onsubmit="return confirm('Remove this product from the store? Its company will no longer see or edit it.');">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="productId" value="${p.productId}">
                                <input type="hidden" name="status" value="${filter}">
                                <input type="hidden" name="q" value="<c:out value='${param.q}'/>">
                                <button type="submit" class="btn btn-danger btn-small">Remove</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>