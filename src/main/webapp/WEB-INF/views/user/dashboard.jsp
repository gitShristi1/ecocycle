<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.userName}"/>!</h1>
<div class="panel">
    <p>This is your EcoCycle dashboard.</p>
    <p class="muted">Selling waste and the recycled product store are coming soon.</p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>