<%--
  Author： HashZhang
  Date: 2016/7/1
  Time: 8:25
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>DashBoard</title>
    <jsp:include page="common/headInclude.jsp"></jsp:include>
</head>

<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="common/header.jsp"></jsp:include>
    <jsp:include page="common/sidebar.jsp">
        <jsp:param name="selected" value="Dashboard"/>
    </jsp:include>
    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                Collecat-Manager
                <small>用于抽取MyCat分布式数据库的中间件</small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">

        </section>
        <jsp:include page="common/footer.jsp"></jsp:include>
    </div>
</div>
<jsp:include page="common/bodyScript.jsp"></jsp:include>
</body>

</html>
