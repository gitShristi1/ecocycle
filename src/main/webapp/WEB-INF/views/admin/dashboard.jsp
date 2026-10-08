<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Admin Dashboard"/>
</jsp:include>

<h1>Welcome, <c:out value="${sessionScope.adminName}"/>!</h1>

<c:if test="${empty stats}">
    <div class="alert alert-error">The statistics could not be loaded right now.</div>
</c:if>

<c:if test="${not empty stats}">
    <h2>Platform overview</h2>
    <div class="stats">
        <div class="stat">
            <div class="num"><c:out value="${stats.users}"/></div>
            <div class="label">Registered users (<c:out value="${stats.blockedUsers}"/> blocked)</div>
        </div>
        <div class="stat">
            <div class="num"><c:out value="${stats.approvedCompanies}"/></div>
            <div class="label">Approved companies</div>
        </div>
        <div class="stat">
            <div class="num">
                <c:out value="${stats.pendingCompanies}"/>
                <c:if test="${stats.pendingCompanies > 0}"><span class="badge">!</span></c:if>
            </div>
            <div class="label"><a href="${pageContext.request.contextPath}/admin/companies">Companies awaiting approval</a></div>
        </div>
        <div class="stat">
            <div class="num"><c:out value="${stats.openRequests}"/></div>
            <div class="label">Open waste requests</div>
        </div>
    </div>

    <div class="stats">
        <div class="stat">
            <div class="num"><fmt:formatNumber value="${stats.wasteCollectedKg}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Waste collected (kg)</div>
        </div>
        <div class="stat">
            <div class="num">&#8377;<fmt:formatNumber value="${stats.totalPayments}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Paid to users for waste</div>
        </div>
        <div class="stat">
            <div class="num"><c:out value="${stats.orders}"/></div>
            <div class="label">Product orders</div>
        </div>
        <div class="stat">
            <div class="num">&#8377;<fmt:formatNumber value="${stats.productSales}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Product sales</div>
        </div>
        <div class="stat">
            <div class="num">&#8377;<fmt:formatNumber value="${stats.commissionEarned}" minFractionDigits="2" maxFractionDigits="2"/></div>
            <div class="label">Commission earned</div>
        </div>
    </div>
</c:if>

<div class="panel">
    <p>Manage <a href="${pageContext.request.contextPath}/admin/companies">companies</a>,
       <a href="${pageContext.request.contextPath}/admin/users">users</a>, and
       <a href="${pageContext.request.contextPath}/admin/pricing">pricing and commission</a>.</p>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>