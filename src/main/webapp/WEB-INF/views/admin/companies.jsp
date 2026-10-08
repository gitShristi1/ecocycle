<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Companies"/>
</jsp:include>

<h1>Companies</h1>

<c:choose>
    <c:when test="${param.msg == 'approve'}"><div class="alert alert-success">Company approved.</div></c:when>
    <c:when test="${param.msg == 'reject'}"><div class="alert alert-success">Company rejected.</div></c:when>
    <c:when test="${param.msg == 'block'}"><div class="alert alert-success">Company blocked. It can no longer log in, and its products are hidden from the store.</div></c:when>
    <c:when test="${param.msg == 'unblock'}"><div class="alert alert-success">Company unblocked.</div></c:when>
    <c:when test="${param.msg == 'stale'}"><div class="alert alert-error">That company's status had already changed. The list has been refreshed.</div></c:when>
</c:choose>

<div class="tabs">
    <c:forEach items="${['PENDING','APPROVED','REJECTED','BLOCKED','ALL']}" var="s">
        <a class="tab ${filter == s ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/companies?status=${s}"><c:out value="${s}"/></a>
    </c:forEach>
</div>

<c:if test="${empty companies}">
    <div class="panel">No companies here.</div>
</c:if>

<c:if test="${not empty companies}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Company</th>
                <th>Email</th>
                <th>Phone</th>
                <th>City</th>
                <th>Registered</th>
                <th>Status</th>
                <th></th>
            </tr>
            <c:forEach items="${companies}" var="co">
                <tr>
                    <td><c:out value="${co.companyName}"/></td>
                    <td><c:out value="${co.email}"/></td>
                    <td><c:out value="${co.phone}"/></td>
                    <td><c:out value="${co.city}"/></td>
                    <td><fmt:formatDate value="${co.createdAt}" pattern="dd MMM yyyy HH:mm"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${co.status == 'BLOCKED'}"><span class="stock-out">BLOCKED</span></c:when>
                            <c:otherwise><c:out value="${co.status}"/></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/admin/companies"
                              style="display:inline;">
                            <input type="hidden" name="companyId" value="${co.companyId}">
                            <input type="hidden" name="status" value="${filter}">
                            <c:choose>
                                <c:when test="${co.status == 'PENDING'}">
                                    <button type="submit" name="action" value="approve" class="btn btn-primary btn-small">Approve</button>
                                    <button type="submit" name="action" value="reject" class="btn btn-danger btn-small">Reject</button>
                                </c:when>
                                <c:when test="${co.status == 'APPROVED'}">
                                    <button type="submit" name="action" value="block" class="btn btn-danger btn-small"
                                            onclick="return confirm('Block this company? It will not be able to log in, and its products will leave the store.');">Block</button>
                                </c:when>
                                <c:when test="${co.status == 'BLOCKED'}">
                                    <button type="submit" name="action" value="unblock" class="btn btn-primary btn-small">Unblock</button>
                                </c:when>
                            </c:choose>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>