<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/fragments/header.jsp">
    <jsp:param name="title" value="Home"/>
</jsp:include>

<section class="hero">
    <h1>Turn your waste into value</h1>
    <p>Sell recyclable waste to registered recycling companies,
       and buy products made from recycled materials.</p>
</section>

<section class="choices">
    <div class="card">
        <h2>For individuals</h2>
        <p>Sell your recyclable waste, track pickups, and shop for recycled products.</p>
        <a class="btn btn-primary" href="login">User Login</a>
        <a class="link" href="register">New here? Create an account</a>
    </div>

    <div class="card">
        <h2>For recycling companies</h2>
        <p>Source waste, list your recycled products, and track your sales.</p>
        <a class="btn btn-primary" href="company-login">Company Login</a>
        <a class="link" href="register-company">Register your company</a>
    </div>
</section>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp"/>