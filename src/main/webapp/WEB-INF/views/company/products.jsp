<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Products"/>
</jsp:include>

<h1>My products</h1>
<p><a class="btn btn-primary btn-small" href="${pageContext.request.contextPath}/company/product-form">Add a product</a></p>

<c:choose>
    <c:when test="${param.msg == 'added'}"><div class="alert alert-success">Product added.</div></c:when>
    <c:when test="${param.msg == 'updated'}"><div class="alert alert-success">Product updated.</div></c:when>
    <c:when test="${param.msg == 'removed'}"><div class="alert alert-success">Product removed.</div></c:when>
    <c:when test="${param.msg == 'invalid'}"><div class="alert alert-error">That product could not be changed.</div></c:when>
    <c:when test="${param.msg == 'notapproved'}"><div class="alert alert-error">Your company is not approved to list products.</div></c:when>
    <c:when test="${param.msg == 'toolarge'}"><div class="alert alert-error">That picture is too large (the limit is 2 MB). Nothing was saved.</div></c:when>
</c:choose>

<c:if test="${empty products}">
    <div class="panel">You have not added any products yet.</div>
</c:if>

<c:if test="${not empty products}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th></th>
                <th>Product</th>
                <th>Price (&#8377;)</th>
                <th>Stock</th>
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
                    <td><c:out value="${p.productName}"/></td>
                    <td><fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.stock == 0}"><span class="stock-out">Out of stock</span></c:when>
                            <c:otherwise><c:out value="${p.stock}"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatDate value="${p.createdAt}" pattern="dd MMM yyyy"/></td>
                    <td>
                        <a class="btn btn-primary btn-small"
                           href="${pageContext.request.contextPath}/company/product-form?id=${p.productId}">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/company/products"
                              style="display:inline;"
                              onsubmit="return confirm('Remove this product from the store?');">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="productId" value="${p.productId}">
                            <button type="submit" class="btn btn-danger btn-small">Remove</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>