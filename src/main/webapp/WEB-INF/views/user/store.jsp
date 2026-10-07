<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Store"/>
</jsp:include>

<h1>Recycled products</h1>

<div class="panel">
    <form method="get" action="${pageContext.request.contextPath}/user/store" class="filters">
        <label>Search<br>
            <input type="text" name="q" maxlength="100" value="<c:out value='${param.q}'/>"
                   placeholder="Name or description">
        </label>
        <label>Max price (&#8377;)<br>
            <input type="number" name="maxPrice" min="0" step="0.01" style="width:7rem;"
                   value="<c:out value='${param.maxPrice}'/>">
        </label>
        <label>Sort by<br>
            <select name="sort">
                <option value="">Newest</option>
                <option value="price_asc" ${param.sort == 'price_asc' ? 'selected' : ''}>Price: low to high</option>
                <option value="price_desc" ${param.sort == 'price_desc' ? 'selected' : ''}>Price: high to low</option>
            </select>
        </label>
        <label>
            <input type="checkbox" name="inStock" value="1" ${param.inStock == '1' ? 'checked' : ''}>
            In stock only
        </label>
        <button type="submit" class="btn btn-primary btn-small">Apply</button>
        <a href="${pageContext.request.contextPath}/user/store">Clear</a>
    </form>
</div>

<c:if test="${empty products}">
    <div class="panel">No products match. Try different filters.</div>
</c:if>

<div class="grid">
    <c:forEach items="${products}" var="p">
        <div class="product">
            <c:choose>
                <c:when test="${not empty p.imagePath}">
                    <img class="product-img"
                         src="${pageContext.request.contextPath}/product-image?f=<c:out value='${p.imagePath}'/>"
                         alt="<c:out value='${p.productName}'/>">
                </c:when>
                <c:otherwise>
                    <div class="product-img product-img-empty">No picture</div>
                </c:otherwise>
            </c:choose>
            <h3><a href="${pageContext.request.contextPath}/user/product?id=${p.productId}"><c:out value="${p.productName}"/></a></h3>
            <div class="muted">by <c:out value="${p.companyName}"/></div>
            <p class="desc"><c:out value="${p.description}"/></p>
            <div class="price">&#8377;<fmt:formatNumber value="${p.price}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <c:choose>
                <c:when test="${p.stock == 0}"><div class="stock-out">Out of stock</div></c:when>
                <c:otherwise>
                    <div class="muted"><c:out value="${p.stock}"/> in stock</div>
                    <form method="post" action="${pageContext.request.contextPath}/user/cart" class="add-form">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="productId" value="${p.productId}">
                        <input type="number" name="quantity" value="1" min="1"
                               max="${p.stock < 99 ? p.stock : 99}">
                        <button type="submit" class="btn btn-primary btn-small">Add to cart</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </c:forEach>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>