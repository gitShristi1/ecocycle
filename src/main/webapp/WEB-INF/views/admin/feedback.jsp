<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Monitoring: Feedback"/>
</jsp:include>

<h1>Monitoring</h1>
<jsp:include page="/WEB-INF/views/fragments/admin-tabs.jsp">
    <jsp:param name="active" value="feedback"/>
</jsp:include>

<c:choose>
    <c:when test="${param.msg == 'deleted'}"><div class="alert alert-success">Review deleted.</div></c:when>
    <c:when test="${param.msg == 'stale'}"><div class="alert alert-error">That review was already deleted. The list has been refreshed.</div></c:when>
</c:choose>

<c:if test="${empty reviews}">
    <div class="panel">No reviews have been written yet.</div>
</c:if>

<c:if test="${not empty reviews}">
    <c:if test="${reviews.size() >= 200}"><p class="muted">Showing the 200 most recent reviews.</p></c:if>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Product</th>
                <th>Reviewer</th>
                <th>Rating</th>
                <th>Review</th>
                <th>Date</th>
                <th></th>
            </tr>
            <c:forEach items="${reviews}" var="f">
                <tr>
                    <td><c:out value="${f.productName}"/></td>
                    <td><c:out value="${f.userName}"/></td>
                    <td><span class="stars"><c:forEach begin="1" end="5" var="n">${n <= f.rating ? '&#9733;' : '&#9734;'}</c:forEach></span></td>
                    <td><c:out value="${f.reviewText}"/></td>
                    <td><fmt:formatDate value="${f.createdAt}" pattern="dd MMM yyyy"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/feedback"
                              onsubmit="return confirm('Delete this review permanently?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="feedbackId" value="${f.feedbackId}">
                            <button type="submit" class="btn btn-danger btn-small">Delete</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>