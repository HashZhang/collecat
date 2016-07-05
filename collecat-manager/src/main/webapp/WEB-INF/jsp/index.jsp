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
<!-- jQuery 2.2.0 -->
<script src="/static/plugins/jQuery/jQuery-2.2.0.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/static/bootstrap/js/bootstrap.min.js"></script>
<!-- Select2 -->
<script src="/static/plugins/select2/select2.full.min.js"></script>
<!-- InputMask -->
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.extensions.js"></script>
<!-- date-range-picker -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.11.2/moment.min.js"></script>
<script src="/static/plugins/daterangepicker/daterangepicker.js"></script>
<!-- bootstrap datepicker -->
<script src="/collecat-manager/static/plugins/datepicker/bootstrap-datepicker.js"></script>
<!-- bootstrap color picker -->
<script src="/collecat-manager/static/plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
<!-- bootstrap time picker -->
<script src="/collecat-manager/static/plugins/timepicker/bootstrap-timepicker.min.js"></script>
<!-- SlimScroll 1.3.0 -->
<script src="/collecat-manager/static/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- iCheck 1.0.1 -->
<script src="/collecat-manager/static/plugins/iCheck/icheck.min.js"></script>
<!-- FastClick -->
<script src="/collecat-manager/static/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="/collecat-manager/static/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/collecat-manager/static/dist/js/demo.js"></script>
</body>

</html>
