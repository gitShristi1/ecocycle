<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Product"/>
</jsp:include>

<p><a href="${pageContext.request.contextPath}/user/store">&larr; Back to the store</a></p>

<c:choose>
    <c:when test="${param.msg == 'reviewed'}"><div class="alert alert-success">Thank you! Your review was saved.</div></c:when>
    <c:when test="${param.msg == 'notbuyer'}"><div class="alert alert-error">Only customers who bought this product can review it.</div></c:when>
    <c:when test="${param.msg == 'invalid'}"><div class="alert alert-error">Please choose a rating from 1 to 5. The review text can be at most 1000 characters.</div></c:when>
</c:choose>

<div class="detail">
    <div class="detail-img">
        <c:choose>
            <c:when test="${not empty product.imagePath}">
                <img class="product-img"
                     src="${pageContext.request.contextPath}/product-image?f=<c:out value='${product.imagePath}'/>"
                     alt="<c:out value='${product.productName}'/>">
            </c:when>
            <c:otherwise>
                <div class="product-img product-img-empty">No picture</div>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="detail-info">
        <h1><c:out value="${product.productName}"/></h1>
        <p class="muted">by <c:out value="${product.companyName}"/></p>
        <div class="price">&#8377;<fmt:formatNumber value="${product.price}" minFractionDigits="2" maxFractionDigits="2"/></div>

        <c:choose>
            <c:when test="${product.stock == 0}"><div class="stock-out">Out of stock</div></c:when>
            <c:otherwise>
                <div class="muted"><c:out value="${product.stock}"/> in stock</div>
                <form method="post" action="${pageContext.request.contextPath}/user/cart" class="add-form">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.productId}">
                    <input type="number" name="quantity" value="1" min="1"
                           max="${product.stock < 99 ? product.stock : 99}">
                    <button type="submit" class="btn btn-primary btn-small">Add to cart</button>
                </form>
            </c:otherwise>
        </c:choose>

        <p class="pre"><c:out value="${product.description}"/></p>
    </div>
</div>

<h2>Reviews</h2>

<c:choose>
    <c:when test="${rating.count == 0}">
        <p class="muted">No reviews yet.</p>
    </c:when>
    <c:otherwise>
        <p><span class="stars">&#9733;</span>
           <strong><fmt:formatNumber value="${rating.average}" minFractionDigits="1" maxFractionDigits="1"/></strong> / 5
           <span class="muted">(<c:out value="${rating.count}"/> review<c:if test="${rating.count != 1}">s</c:if>)</span></p>
    </c:otherwise>
</c:choose>

<c:forEach items="${reviews}" var="f">
    <div class="review">
        <span class="stars"><c:forEach begin="1" end="5" var="n">${n <= f.rating ? '&#9733;' : '&#9734;'}</c:forEach></span>
        <strong><c:out value="${f.displayName}"/></strong>
        <span class="muted"><fmt:formatDate value="${f.createdAt}" pattern="dd MMM yyyy"/></span>
        <c:if test="${not empty f.reviewText}">
            <p><c:out value="${f.reviewText}"/></p>
        </c:if>
    </div>
</c:forEach>

<c:choose>
    <c:when test="${canReview}">
        <div class="panel">
            <h3><c:out value="${empty myReview ? 'Write a review' : 'Edit your review'}"/></h3>
            <form method="post" action="${pageContext.request.contextPath}/user/review">
                <input type="hidden" name="productId" value="${product.productId}">
                <p><label>Rating *<br>
                    <select name="rating" required>
                        <option value="">Choose...</option>
                        <c:forEach begin="1" end="5" var="n">
                            <option value="${n}" ${myReview.rating == n ? 'selected' : ''}>${n} star${n > 1 ? 's' : ''}</option>
                        </c:forEach>
                    </select>
                </label></p>
                <p><label>Your review <small>(optional)</small><br>
                    <textarea name="reviewText" maxlength="1000" rows="4"
                              style="width:100%;"><c:out value="${myReview.reviewText}"/></textarea>
                </label></p>
                <button type="submit" class="btn btn-primary">Save review</button>
            </form>
        </div>
    </c:when>
    <c:otherwise>
        <p class="muted">Only customers who have bought this product can review it.</p>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>