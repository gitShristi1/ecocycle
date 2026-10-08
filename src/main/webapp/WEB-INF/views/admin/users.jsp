<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Users"/>
</jsp:include>

<h1>Users</h1>

<c:choose>
    <c:when test="${param.msg == 'block'}"><div class="alert alert-success">User blocked. They can no longer log in.</div></c:when>
    <c:when test="${param.msg == 'unblock'}"><div class="alert alert-success">User unblocked.</div></c:when>
    <c:when test="${param.msg == 'stale'}"><div class="alert alert-error">That user's status had already changed. The list has been refreshed.</div></c:when>
</c:choose>

<div class="panel">
    <form method="get" action="${pageContext.request.contextPath}/admin/users" class="filters">
        <label>Search by name or email<br>
            <input type="text" name="q" maxlength="100" value="<c:out value='${param.q}'/>">
        </label>
        <button type="submit" class="btn btn-primary btn-small">Search</button>
        <a href="${pageContext.request.contextPath}/admin/users">Clear</a>
    </form>
</div>

<c:if test="${empty users}">
    <div class="panel">No users found.</div>
</c:if>

<c:if test="${not empty users}">
    <p class="muted">Showing up to 200 users, newest first.</p>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>City</th>
                <th>Registered</th>
                <th>Status</th>
                <th></th>
            </tr>
            <c:forEach items="${users}" var="u">
                <tr>
                    <td><c:out value="${u.fullName}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td><c:out value="${u.phone}"/></td>
                    <td><c:out value="${u.city}"/></td>
                    <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${u.status == 'BLOCKED'}"><span class="stock-out">BLOCKED</span></c:when>
                            <c:otherwise><c:out value="${u.status}"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/users"
                              onsubmit="return confirm('${u.status == 'ACTIVE' ? 'Block' : 'Unblock'} this user?');">
                            <input type="hidden" name="userId" value="${u.userId}">
                            <input type="hidden" name="q" value="<c:out value='${param.q}'/>">
                            <c:choose>
                                <c:when test="${u.status == 'ACTIVE'}">
                                    <button type="submit" name="action" value="block" class="btn btn-danger btn-small">Block</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="submit" name="action" value="unblock" class="btn btn-primary btn-small">Unblock</button>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>