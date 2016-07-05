<%@ page import="com.sf.collecat.common.model.Node" %>
<%@ page import="com.sf.collecat.common.utils.StrUtils" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%--
  Author： HashZhang
  Date: 2016/7/4
  Time: 10:07
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>Task</title>
    <jsp:include page="../common/headInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../common/header.jsp"></jsp:include>
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="selected" value="node"/>
    </jsp:include>
    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                Node
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/collecat-manager/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/collecat-manager/node.do">Node</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content" style="overflow:scroll;height:inherit">
            <div class="row">
                <label class="text-red"><b>
                    <%
                        String message = (String) request.getAttribute("message");
                        out.print(StrUtils.transferNull(message));
                    %>
                </b></label>
            </div>
            <%
                SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                List<Node> nodes = (List<Node>) request.getAttribute("allNodes");
                out.print("<table id=\"allJobs\" class=\"table table-bordered table-striped\" cellspacing=\"0\" width=\"100%\">\n" +
                        "        <thead>\n" +
                        "            <tr>\n" +
                        "                <th>ID</th>\n" +
                        "                <th>IP</th>\n" +
                        "                <th>CURRENT NODE ID</th>\n" +
                        "            </tr>\n" +
                        "        </thead>\n" +
                        "        <tfoot>\n" +
                        "        </tfoot>\n" +
                        "        <tbody>\n");
                for (Node node : nodes) {
                    out.print("<tr>" +
                            "<td>" + node.getId() + "</td>" +
                            "<td>" + node.getIp() + "</td>" +
                            "<td>" + node.getCurrentNodeId() + "</td>" +
                            "</tr>");
                }
                out.print("        </tbody>\n" +
                        "    </table>");
            %>
            <div class="box box-primary">
                <div class="box-header">
                    <h3 class="box-title">#注释：</h3>
                </div>
                <div class="box-body">
                    <ol>
                        <li>IP: node所在机器的ip</li>
                        <li>CURRENT NODE ID: node所在zk上的id</li>
                    </ol>
                </div>
            </div>
        </section>
        <jsp:include page="../common/footer.jsp"></jsp:include>
    </div>
</div>
<!-- jQuery 2.2.0 -->
<script src="/collecat-manager/static/plugins/jQuery/jQuery-2.2.0.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/collecat-manager/static/bootstrap/js/bootstrap.min.js"></script>
<!-- DataTables -->
<script src="/collecat-manager/static/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="/collecat-manager/static/plugins/datatables/dataTables.bootstrap.min.js"></script>
<!-- SlimScroll -->
<script src="/collecat-manager/static/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="/collecat-manager/static/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="/collecat-manager/static/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/collecat-manager/static/dist/js/demo.js"></script>
<!-- page script -->
<script>
    $(function () {
        $("#allJobs").DataTable();
    });
</script>
</body>
</html>
