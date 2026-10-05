<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Company Approvals"/>
</jsp:include>

<h1>Pending company registrations</h1>

<c:choose>
    <c:when test="${param.msg == 'approve'}">
        <div class="alert alert-success">Company approved.</div>
    </c:when>
    <c:when test="${param.msg == 'reject'}">
        <div class="alert alert-success">Company rejected.</div>
    </c:when>
    <c:when test="${param.msg == 'stale'}">
        <div class="alert alert-error">That company was already handled. The list has been refreshed.</div>
    </c:when>
</c:choose>

<c:if test="${empty pending}">
    <div class="panel">No companies are waiting for approval.</div>
</c:if>

<c:if test="${not empty pending}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Company</th>
                <th>Email</th>
                <th>Phone</th>
                <th>City</th>
                <th>Registered</th>
                <th>Action</th>
            </tr>
            <c:forEach items="${pending}" var="co">
                <tr>
                    <td><c:out value="${co.companyName}"/></td>
                    <td><c:out value="${co.email}"/></td>
                    <td><c:out value="${co.phone}"/></td>
                    <td><c:out value="${co.city}"/></td>
                    <td><fmt:formatDate value="${co.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/companies"
                              style="display:inline;">
                            <input type="hidden" name="companyId" value="${co.companyId}">
                            <button type="submit" name="action" value="approve" class="btn btn-primary btn-small">Approve</button>
                            <button type="submit" name="action" value="reject" class="btn btn-danger btn-small">Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>