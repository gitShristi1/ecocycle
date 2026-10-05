<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Company Login"/>
</jsp:include>

<div class="form-card">
    <h1>Company Login</h1>

    <c:if test="${param.registered == '1'}">
        <div class="alert alert-success">Registration submitted. You can log in once the admin approves your company.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/company-login">
        <label>Email
            <input type="email" name="email" value="<c:out value='${param.email}'/>" required>
        </label>
        <label>Password
            <input type="password" name="password" required>
        </label>
        <button type="submit" class="btn btn-primary btn-block">Log in</button>
    </form>

    <p class="form-note">Not registered yet? <a href="${pageContext.request.contextPath}/register-company">Register your company</a></p>
    <p class="form-note">Individual user? <a href="${pageContext.request.contextPath}/login">Log in here</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>