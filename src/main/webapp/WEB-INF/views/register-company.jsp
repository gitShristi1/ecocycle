<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Register Your Company"/>
</jsp:include>

<div class="form-card">
    <h1>Register your company</h1>
    <p class="muted">New companies must be approved by the admin before they can log in.</p>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="e">
                    <li><c:out value="${e}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register-company">
        <label>Company name *
            <input type="text" name="companyName" maxlength="150"
                   value="<c:out value='${param.companyName}'/>" required>
        </label>
        <label>Email *
            <input type="email" name="email" maxlength="150"
                   value="<c:out value='${param.email}'/>" required>
        </label>
        <label>Password * <small>(8+ characters, at least one letter and one digit)</small>
            <input type="password" name="password" minlength="8" maxlength="72" required>
        </label>
        <label>Confirm password *
            <input type="password" name="confirmPassword" minlength="8" maxlength="72" required>
        </label>
        <label>Phone <small>(10 digits)</small>
            <input type="text" name="phone" maxlength="10" pattern="[0-9]{10}"
                   value="<c:out value='${param.phone}'/>">
        </label>
        <label>Address
            <input type="text" name="address" maxlength="300"
                   value="<c:out value='${param.address}'/>">
        </label>
        <label>City
            <input type="text" name="city" maxlength="80"
                   value="<c:out value='${param.city}'/>">
        </label>
        <button type="submit" class="btn btn-primary btn-block">Submit registration</button>
    </form>

    <p class="form-note">Already registered? <a href="${pageContext.request.contextPath}/company-login">Log in</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>