<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Pricing"/>
</jsp:include>

<h1>Waste rates</h1>
<p class="muted">Rates apply to new requests only. Existing requests keep the rate they were submitted with.</p>

<c:choose>
    <c:when test="${param.msg == 'commission'}"><div class="alert alert-success">Commission updated. It applies to new orders only.</div></c:when>
    <c:when test="${param.msg == 'badcommission'}"><div class="alert alert-error">The commission must be between 0 and 50 percent, with at most 2 decimals.</div></c:when>
    <c:when test="${param.msg == 'updated'}"><div class="alert alert-success">Rate updated.</div></c:when>
    <c:when test="${param.msg == 'added'}"><div class="alert alert-success">Waste type added.</div></c:when>
    <c:when test="${param.msg == 'toggled'}"><div class="alert alert-success">Waste type updated.</div></c:when>
    <c:when test="${param.msg == 'exists'}"><div class="alert alert-error">A waste type with that name already exists.</div></c:when>
    <c:when test="${param.msg == 'invalid'}"><div class="alert alert-error">Invalid input. Rates must be between 0 and 10000 with at most 2 decimals.</div></c:when>
</c:choose>

<div class="panel">
    <h2>Platform commission</h2>
    <p class="muted">The share of every product sale that the platform keeps. Orders already placed keep the percentage they were sold with.</p>
    <form method="post" action="${pageContext.request.contextPath}/admin/pricing"
          style="display:flex; gap:0.6rem; flex-wrap:wrap; align-items:flex-end;">
        <input type="hidden" name="action" value="commission">
        <label>Commission (%)<br>
            <input type="number" name="percent" min="0" max="50" step="0.01"
                   value="<c:out value='${commission}'/>" required style="width:8rem;">
        </label>
        <button type="submit" class="btn btn-primary">Save</button>
    </form>
</div>    
    
<div class="table-wrap">
    <table class="table">
        <tr>
            <th>Waste type</th>
            <th>Rate per kg (&#8377;)</th>
            <th>Available to users</th>
        </tr>
        <c:forEach items="${types}" var="t">
            <tr>
                <td><c:out value="${t.typeName}"/></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/pricing"
                          style="display:flex; gap:0.4rem;">
                        <input type="hidden" name="action" value="rate">
                        <input type="hidden" name="id" value="${t.wasteTypeId}">
                        <input type="number" name="rate" value="${t.ratePerKg}"
                               min="0" max="10000" step="0.01" required style="width:7rem;">
                        <button type="submit" class="btn btn-primary btn-small">Save</button>
                    </form>
                </td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/admin/pricing">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="${t.wasteTypeId}">
                        <c:choose>
                            <c:when test="${t.active}">
                                Yes
                                <input type="hidden" name="active" value="N">
                                <button type="submit" class="btn btn-danger btn-small">Turn off</button>
                            </c:when>
                            <c:otherwise>
                                No
                                <input type="hidden" name="active" value="Y">
                                <button type="submit" class="btn btn-primary btn-small">Turn on</button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<div class="panel" style="margin-top:1.5rem;">
    <h2>Add a waste type</h2>
    <form method="post" action="${pageContext.request.contextPath}/admin/pricing"
          style="display:flex; gap:0.6rem; flex-wrap:wrap; align-items:flex-end;">
        <input type="hidden" name="action" value="add">
        <label>Name<br>
            <input type="text" name="typeName" maxlength="50" required>
        </label>
        <label>Rate per kg (&#8377;)<br>
            <input type="number" name="rate" min="0" max="10000" step="0.01" required style="width:8rem;">
        </label>
        <button type="submit" class="btn btn-primary">Add</button>
    </form>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>