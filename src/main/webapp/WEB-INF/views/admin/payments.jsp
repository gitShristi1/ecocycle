<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Monitoring: Payments"/>
</jsp:include>

<h1>Monitoring</h1>
<jsp:include page="/WEB-INF/views/fragments/admin-tabs.jsp">
    <jsp:param name="active" value="payments"/>
</jsp:include>

<c:if test="${empty payments}">
    <div class="panel">No payments have been recorded yet.</div>
</c:if>

<c:if test="${not empty payments}">
    <div class="panel">
        Total of the payments listed:
        <strong>&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2" maxFractionDigits="2"/></strong>
        <c:if test="${payments.size() >= 200}"> <span class="muted">(the 200 most recent)</span></c:if>
    </div>
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Payment</th>
                <th>Request</th>
                <th>Paid to</th>
                <th>Paid by</th>
                <th>Waste</th>
                <th>Amount (&#8377;)</th>
                <th>Date</th>
            </tr>
            <c:forEach items="${payments}" var="p">
                <tr>
                    <td>#<c:out value="${p.paymentId}"/></td>
                    <td>#<c:out value="${p.requestId}"/></td>
                    <td><c:out value="${p.userName}"/></td>
                    <td><c:out value="${p.companyName}"/></td>
                    <td><c:out value="${p.wasteTypeName}"/></td>
                    <td><fmt:formatNumber value="${p.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatDate value="${p.paidAt}" pattern="dd MMM yyyy HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>