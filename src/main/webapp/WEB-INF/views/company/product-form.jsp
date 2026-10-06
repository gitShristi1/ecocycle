<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Product"/>
</jsp:include>

<div class="form-card">
    <h1><c:out value="${empty editId ? 'Add a product' : 'Edit product'}"/></h1>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="e">
                    <li><c:out value="${e}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" enctype="multipart/form-data"
          action="${pageContext.request.contextPath}/company/product-form">
        <c:if test="${not empty editId}">
            <input type="hidden" name="id" value="${editId}">
        </c:if>
        <label>Product name *
            <input type="text" name="productName" maxlength="150"
                   value="<c:out value='${fName}'/>" required>
        </label>
        <label>Description
            <textarea name="description" maxlength="1000" rows="4"><c:out value="${fDescription}"/></textarea>
        </label>
        <label>Price (&#8377;) *
            <input type="number" name="price" min="0.01" max="1000000" step="0.01"
                   value="<c:out value='${fPrice}'/>" required>
        </label>
        <label>Stock (units available) *
            <input type="number" name="stock" min="0" max="100000" step="1"
                   value="<c:out value='${fStock}'/>" required>
        </label>

        <c:if test="${not empty currentImage}">
            <p>Current picture:<br>
               <img class="product-img" style="max-width:240px;"
                    src="${pageContext.request.contextPath}/product-image?f=<c:out value='${currentImage}'/>"
                    alt="Current product picture"></p>
        </c:if>
        <label>Picture <small>(JPEG or PNG, up to 2 MB<c:if test="${not empty currentImage}">; leave empty to keep the current one</c:if>)</small>
            <input type="file" name="image" accept="image/jpeg,image/png">
        </label>

        <button type="submit" class="btn btn-primary btn-block">Save product</button>
    </form>
    <p class="form-note"><a href="${pageContext.request.contextPath}/company/products">Cancel</a></p>
</div>

<script>
document.querySelector('input[name="image"]').addEventListener('change', function () {
    if (this.files[0] && this.files[0].size > 2 * 1024 * 1024) {
        alert('The picture must be 2 MB or smaller.');
        this.value = '';
    }
});
</script>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>