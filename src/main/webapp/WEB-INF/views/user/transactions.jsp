<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="My Payments"/>
</jsp:include>

<h1>My payments</h1>

<div class="panel">
    Total received:
    <strong>&#8377;<fmt:formatNumber value="${totalReceived}" minFractionDigits="2" maxFractionDigits="2"/></strong>
</div>

<c:if test="${empty payments}">
    <div class="panel">
        No payments yet. Payments appear here once a company has collected your waste and recorded the payment.
    </div>
</c:if>

<c:if test="${not empty payments}">
    <div class="table-wrap">
        <table class="table">
            <tr>
                <th>Request #</th>
                <th>Waste</th>
                <th>Paid by</th>
                <th>Amount (&#8377;)</th>
                <th>Date</th>
            </tr>
            <c:forEach items="${payments}" var="p">
                <tr>
                    <td><c:out value="${p.requestId}"/></td>
                    <td><c:out value="${p.wasteTypeName}"/></td>
                    <td><c:out value="${p.companyName}"/></td>
                    <td><fmt:formatNumber value="${p.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
                    <td><fmt:formatDate value="${p.paidAt}" pattern="dd MMM yyyy HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>