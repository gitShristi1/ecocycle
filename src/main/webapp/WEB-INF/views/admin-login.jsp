<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Admin Login"/>
</jsp:include>

<div class="form-card">
    <h1>Admin Login</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/admin-login">
        <label>Email
            <input type="email" name="email" value="<c:out value='${param.email}'/>" required>
        </label>
        <label>Password
            <input type="password" name="password" required>
        </label>
        <button type="submit" class="btn btn-primary btn-block">Log in</button>
    </form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>