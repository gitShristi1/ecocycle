<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Reviews"/>
</jsp:include>

<h1>Customer reviews</h1>

<c:choose>
    <c:when test="${rating.count == 0}">
        <div class="panel">No reviews yet. Customers can review a product after they have bought it.</div>
    </c:when>
    <c:otherwise>
        <div class="stats">
            <div class="stat">
                <div class="num"><span class="stars">&#9733;</span>
                    <fmt:formatNumber value="${rating.average}" minFractionDigits="1" maxFractionDigits="1"/></div>
                <div class="label">Average rating</div>
            </div>
            <div class="stat">
                <div class="num"><c:out value="${rating.count}"/></div>
                <div class="label">Reviews</div>
            </div>
        </div>

        <div class="table-wrap">
            <table class="table">
                <tr>
                    <th>Product</th>
                    <th>Rating</th>
                    <th>Review</th>
                    <th>Customer</th>
                    <th>Date</th>
                </tr>
                <c:forEach items="${reviews}" var="f">
                    <tr>
                        <td><c:out value="${f.productName}"/></td>
                        <td><span class="stars"><c:forEach begin="1" end="5" var="n">${n <= f.rating ? '&#9733;' : '&#9734;'}</c:forEach></span></td>
                        <td><c:out value="${f.reviewText}"/></td>
                        <td><c:out value="${f.displayName}"/></td>
                        <td><fmt:formatDate value="${f.createdAt}" pattern="dd MMM yyyy"/></td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>