<%@ page import="com.alibaba.fastjson.JSON" %>
<%@ page import="com.sf.collecat.common.utils.StrUtils" %>
<%@ page import="com.sf.collecat.manager.webapp.common.CytoscapeElements" %>
<%--
  Author： HashZhang
  Date: 2016/7/1
  Time: 10:07
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>Task DashBoard</title>
    <jsp:include page="../common/headInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../common/header.jsp"></jsp:include>
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="selected" value="task"/>
    </jsp:include>
    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                Task
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/collecat-manager/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/collecat-manager/task.do">Task</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content"  style="width: 100%;height: 100%; overflow:scroll;height:inherit">
            <div class="col-lg-2 col-sm-2">
                <button onclick="allTask()" type="button" class="btn btn-block btn-primary">查看列表</button>
            </div>
            <div class="col-lg-2 col-sm-2">
                <button onclick="addTask()" type="button" class="btn btn-block btn-primary">添加Task</button>
            </div>
            <div class="col-lg-2 col-sm-2">
                <button onclick="batchAddTask()" type="button" class="btn btn-block btn-primary">批量添加Task</button>
            </div>
            <div class="row">
                <label class="text-red"><b>
                    <%
                        String message = (String) request.getAttribute("message");
                        out.print(StrUtils.transferNull(message));
                    %>
                </b></label>
            </div>
            <div id="cy" style="width: 100%;height: 100%;">

            </div>
        </section>

        <jsp:include page="../common/footer.jsp"></jsp:include>
    </div>
</div>
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
<script src="/collecat-manager/static/dist/js/cytoscape.min.js"></script>

<script src="/collecat-manager/static/dist/js/dagre.min.js"></script>
<script src="/collecat-manager/static/dist/js/cytoscape-dagre.js"></script>
<script>
    function allTask() {
        window.location.href = "/collecat-manager/task/all.do"
    }
    function addTask() {
        window.location.href = "/collecat-manager/task/publish.do"
    }
    function batchAddTask() {
        window.location.href = "/collecat-manager/task/batchPublish.do"
    }
</script>
<%
    out.print("<script>");

    out.print("$(function() { // on dom ready\n" +
            "\n" +
            "\t\t\t\tvar cy = cytoscape({\n" +
            "\t\t\t\t\tlayout: {\n" +
            "\t\t\t\t\t\tname: 'dagre',\n" +
            "\t\t\t\t\t\tpadding: 10\n" +
            "\t\t\t\t\t},\n" +
            "\t\t\t\t\tcontainer: document.getElementById('cy'),\n" +
            "\t\t\t\t\tstyle: cytoscape.stylesheet()\n" +
            "\t\t\t\t\t\t.selector('node')\n" +
            "\t\t\t\t\t\t.css({\n" +
            "\t\t\t\t\t\t\t'shape': 'data(faveShape)',\n" +
            "\t\t\t\t\t\t\t'width': 'mapData(weight, 40, 80, 20, 60)',\n" +
            "\t\t\t\t\t\t\t'content': 'data(name)',\n" +
            "\t\t\t\t\t\t\t'text-valign': 'center',\n" +
            "\t\t\t\t\t\t\t'text-outline-width': 2,\n" +
            "\t\t\t\t\t\t\t'text-outline-color': 'data(faveColor)',\n" +
            "\t\t\t\t\t\t\t'background-color': 'data(faveColor)',\n" +
            "\t\t\t\t\t\t\t'color': '#fff'\n" +
            "\t\t\t\t\t\t})\n" +
            "\t\t\t\t\t\t.selector(':selected')\n" +
            "\t\t\t\t\t\t.css({\n" +
            "\t\t\t\t\t\t\t'border-width': 3,\n" +
            "\t\t\t\t\t\t\t'border-color': '#333'\n" +
            "\t\t\t\t\t\t})\n" +
            "\t\t\t\t\t\t.selector('edge')\n" +
            "\t\t\t\t\t\t.css({\n" +
            "\t\t\t\t\t\t\t'curve-style': 'bezier',\n" +
            "\t\t\t\t\t\t\t'opacity': 0.666,\n" +
            "\t\t\t\t\t\t\t'label': 'data(label)',\n" +
            "\t\t\t\t\t\t\t'width': 'mapData(strength, 70, 100, 2, 6)',\n" +
            "\t\t\t\t\t\t\t'target-arrow-shape': 'triangle',\n" +
            "\t\t\t\t\t\t\t'source-arrow-shape': 'circle',\n" +
            "\t\t\t\t\t\t\t'line-color': 'data(faveColor)',\n" +
            "\t\t\t\t\t\t\t'source-arrow-color': 'data(faveColor)',\n" +
            "\t\t\t\t\t\t\t'target-arrow-color': 'data(faveColor)'\n" +
            "\t\t\t\t\t\t})\n" +
            "\t\t\t\t\t\t.selector('edge.questionable')\n" +
            "\t\t\t\t\t\t.css({\n" +
            "\t\t\t\t\t\t\t'line-style': 'dotted',\n" +
            "\t\t\t\t\t\t\t'target-arrow-shape': 'diamond'\n" +
            "\t\t\t\t\t\t})\n" +
            "\t\t\t\t\t\t.selector('.faded')\n" +
            "\t\t\t\t\t\t.css({\n" +
            "\t\t\t\t\t\t\t'opacity': 0.25,\n" +
            "\t\t\t\t\t\t\t'text-opacity': 0\n" +
            "\t\t\t\t\t\t}),\n" +
            "\n" +
            "\t\t\t\t\telements: ");
    CytoscapeElements elements = (CytoscapeElements) request.getAttribute("elements");
    out.print(JSON.toJSONString(elements));

    out.print(",ready: function() {\n" +
            "\t\t\t\t\t\twindow.cy = this;\n" +
            "\n" +
            "\t\t\t\t\t\t// giddy up\n" +
            "\t\t\t\t\t}\n" +
            "\t\t\t\t});\n" +
            "\t\t\t\tcy.on('tap', 'node', {\n" +
            "\t\t\t\t\tfoo: 'bar'\n" +
            "\t\t\t\t}, function(evt) {\n" +
            "\t\t\t\t\tvar node = evt.cyTarget;\n" +
            "if(node.id().indexOf(\"//\")<0)"+
            "\t\t\t\t\twindow.location.href = \"./task/route.do?req=\" + node.id();\n" +
            "\t\t\t\t});\n" +
            "\n" +
            "\t\t\t});");

    out.print("</script>");
%>
</body>
</html>
