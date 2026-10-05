<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Create Account"/>
</jsp:include>

<div class="form-card">
    <h1>Create your account</h1>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="e">
                    <li><c:out value="${e}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <label>Full name *
            <input type="text" name="fullName" maxlength="100"
                   value="<c:out value='${param.fullName}'/>" required>
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
        <button type="submit" class="btn btn-primary btn-block">Register</button>
    </form>

    <p class="form-note">Already have an account? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>