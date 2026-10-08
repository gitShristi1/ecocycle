<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<div class="tabs">
    <a class="tab ${param.active == 'requests' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/requests">Waste requests</a>
    <a class="tab ${param.active == 'payments' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/payments">Payments</a>
    <a class="tab ${param.active == 'orders' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders">Orders</a>
    <a class="tab ${param.active == 'products' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/products">Products</a>
    <a class="tab ${param.active == 'feedback' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/feedback">Feedback</a>
</div>