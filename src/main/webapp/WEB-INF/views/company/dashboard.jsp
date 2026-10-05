<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Company Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.companyName}"/>!</h1>
<div class="panel">
    <p>This is your company dashboard.</p>
    <p class="muted">Waste requests and product management are coming soon.</p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>