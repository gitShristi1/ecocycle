<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Company Approvals - EcoCycle</title>
</head>
<body>
    <h1>Pending company registrations</h1>
    <p><a href="${pageContext.request.contextPath}/admin/dashboard">&larr; Back to dashboard</a></p>

    <c:choose>
        <c:when test="${param.msg == 'approve'}">
            <p style="color:green;">Company approved.</p>
        </c:when>
        <c:when test="${param.msg == 'reject'}">
            <p style="color:green;">Company rejected.</p>
        </c:when>
        <c:when test="${param.msg == 'stale'}">
            <p style="color:red;">That company was already handled. The list has been refreshed.</p>
        </c:when>
    </c:choose>

    <c:if test="${empty pending}">
        <p>No companies are waiting for approval.</p>
    </c:if>

    <c:if test="${not empty pending}">
        <table border="1" cellpadding="6" cellspacing="0">
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
                            <button type="submit" name="action" value="approve">Approve</button>
                            <button type="submit" name="action" value="reject">Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</body>
</html>