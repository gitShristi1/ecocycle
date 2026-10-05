<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Sell Waste"/>
</jsp:include>

<div class="form-card">
    <h1>Sell your waste</h1>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="e">
                    <li><c:out value="${e}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:if test="${empty wasteTypes}">
        <div class="alert alert-error">No waste types are available right now.</div>
    </c:if>

    <c:if test="${not empty wasteTypes}">
        <form method="post" action="${pageContext.request.contextPath}/user/sell">
            <label>Waste type *
                <select name="wasteTypeId" required>
                    <option value="">Choose...</option>
                    <c:forEach items="${wasteTypes}" var="t">
                        <option value="${t.wasteTypeId}" data-rate="${t.ratePerKg}"
                            ${param.wasteTypeId == t.wasteTypeId ? 'selected' : ''}>
                            <c:out value="${t.typeName}"/> (&#8377;<c:out value="${t.ratePerKg}"/>/kg)
                        </option>
                    </c:forEach>
                </select>
            </label>
            
            <label>Weight in kg *
                <input type="number" name="weightKg" min="0.01" max="10000" step="0.01"
                       value="<c:out value='${param.weightKg}'/>" required>
            </label>
            <div id="estimate" class="alert alert-success" hidden></div>
            
            <label>Pickup address *
                <input type="text" name="pickupAddress" maxlength="300"
                       value="<c:out value='${formAddress}'/>" required>
            </label>
            <label>City
                <input type="text" name="city" maxlength="80"
                       value="<c:out value='${formCity}'/>">
            </label>
            <label>Notes <small>(optional)</small>
                <textarea name="notes" maxlength="500" rows="3"><c:out value="${param.notes}"/></textarea>
            </label>
            <button type="submit" class="btn btn-primary btn-block">Submit request</button>
        </form>
        <p class="form-note">The amount is calculated from the current rate when you submit.</p>
    </c:if>
</div>

<script>
(function () {
    var typeSelect = document.querySelector('select[name="wasteTypeId"]');
    var weightInput = document.querySelector('input[name="weightKg"]');
    var estimate = document.getElementById('estimate');
    if (!typeSelect || !weightInput || !estimate) { return; }

    function update() {
        var option = typeSelect.options[typeSelect.selectedIndex];
        var rate = parseFloat(option.getAttribute('data-rate'));
        var weight = parseFloat(weightInput.value);
        if (isNaN(rate) || isNaN(weight) || weight <= 0) {
            estimate.hidden = true;
            return;
        }
        estimate.textContent = 'Estimated amount: \u20B9' + (rate * weight).toFixed(2);
        estimate.hidden = false;
    }

    typeSelect.addEventListener('change', update);
    weightInput.addEventListener('input', update);
    update();   // also runs once, in case the form was redisplayed with values
})();
</script>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>