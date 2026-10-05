<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="User Login"/>
</jsp:include>

<div class="form-card">
    <h1>User Login</h1>

    <c:if test="${param.registered == '1'}">
        <div class="alert alert-success">Account created. Please log in.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label>Email
            <input type="email" name="email" value="<c:out value='${param.email}'/>" required>
        </label>
        <label>Password
            <input type="password" name="password" required>
        </label>
        <button type="submit" class="btn btn-primary btn-block">Log in</button>
    </form>

    <p class="form-note">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    <p class="form-note">Recycling company? <a href="${pageContext.request.contextPath}/company-login">Log in here</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>