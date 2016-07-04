<%@ page import="com.sf.collecat.common.model.Task" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %><%--
  Author： HashZhang
  Date: 2016/7/1
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
        <jsp:param name="selected" value="task"/>
    </jsp:include>
    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                Task
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/task.do">Task</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content" style="overflow:scroll;height:inherit">
            <div class="row col-lg-4 col-sm-4">
                <button onclick="addTask()" type="button" class="btn btn-block btn-primary">添加Task</button>
            </div>
            <%
                SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                List<Task> tasks = (List<Task>) request.getAttribute("alltasks");
                out.print("<table id=\"allTasks\" class=\"table table-bordered table-striped\" cellspacing=\"0\" width=\"100%\">\n" +
                        "        <thead>\n" +
                        "            <tr>\n" +
                        "                <th>  </th>\n" +
                        "                <th>ID</th>\n" +
                        "                <th>Initial SQL</th>\n" +
                        "                <th>Schema Used</th>\n" +
                        "                <th>Time Field</th>\n" +
                        "                <th>Last Time</th>\n" +
                        "                <th>Routine Time</th>\n" +
                        "                <th>Allocate Routine</th>\n" +
                        "                <th>Is Active</th>\n" +
                        "                <th>Kafka Topic</th>\n" +
                        "                <th>Kafka Url</th>\n" +
                        "                <th>Kafka Cluster Name</th>\n" +
                        "                <th>Kafka Topic Tokens</th>\n" +
                        "                <th>Kafka Message Size</th>\n" +
                        "                <th>Message Format</th>\n" +
                        "            </tr>\n" +
                        "        </thead>\n" +
                        "        <tfoot>\n" +
                        "        </tfoot>\n" +
                        "        <tbody>\n");
                for (Task task : tasks) {
                    out.print("<tr>" +
                            "<td><button onclick=\"modifyTask(" + task.getId() + ")\" type=\"button\" class=\"btn btn-block btn-primary\">修改</button>" +
                            "<button onclick=\"removeTask(" + task.getId() + ")\" type=\"button\" class=\"btn btn-block btn-danger\">删除</button></td>" +
                            "<td>" + task.getId() + "</td>" +
                            "<td>" + task.getInitialSql() + "</td>" +
                            "<td>" + task.getSchemaUsed() + "</td>" +
                            "<td>" + task.getTimeField() + "</td>" +
                            "<td>" + formatter1.format(task.getLastTime()) + "</td>" +
                            "<td>" + task.getRoutineTime() + "</td>" +
                            "<td>" + task.getAllocateRoutine() + "</td>" +
                            "<td>" + task.getIsActive() + "</td>" +
                            "<td>" + task.getKafkaTopic() + "</td>" +
                            "<td>" + task.getKafkaUrl() + "</td>" +
                            "<td>" + task.getKafkaClusterName() + "</td>" +
                            "<td>" + task.getKafkaTopicTokens() + "</td>" +
                            "<td>" + task.getKafkaMessageSize() + "</td>" +
                            "<td>" + task.getMessageFormat() + "</td>" +
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
                        <li>Initial SQL: 抽取表的初始SQL，决定如何筛选出需要的数据的SQL语句</li>
                        <li>Schema Used: 位于MyCat的哪个逻辑库schema上</li>
                        <li>Time Field: 抽取表根据的时间字段</li>
                        <li>Last Time: 上次生成job的截止时间</li>
                        <li>Routine Time: 每个job抽取时间长度</li>
                        <li>Allocate Routine: 调度cron表达式，决定多久让task生成一次job</li>
                        <li>Is Active:这个task是否处于活跃状态，如果非活跃状态，不会生成job</li>
                        <li>Kafka Topic:抽取到的kafka topic</li>
                        <li>Kafka Url:抽取到的kafka url</li>
                        <li>Kafka Cluster Name:抽取到的kafka Cluster Name</li>
                        <li>Kafka Topic Tokens:抽取到的kafka token</li>
                        <li>Kafka Message Size:抽取到的kafka每条message最多包含多少条记录</li>
                        <li>Message Format:数据格式，目前支持csv和json</li>
                    </ol>
                </div>
            </div>
        </section>
        <jsp:include page="../common/footer.jsp"></jsp:include>
    </div>
</div>
<!-- jQuery 2.2.0 -->
<script src="/static/plugins/jQuery/jQuery-2.2.0.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/static/bootstrap/js/bootstrap.min.js"></script>
<!-- Select2 -->
<script src="/static/plugins/select2/select2.full.min.js"></script>
<!-- InputMask -->
<script src="/static/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/static/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/static/plugins/input-mask/jquery.inputmask.extensions.js"></script>
<!-- date-range-picker -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.11.2/moment.min.js"></script>
<script src="/static/plugins/daterangepicker/daterangepicker.js"></script>
<!-- bootstrap datepicker -->
<script src="/static/plugins/datepicker/bootstrap-datepicker.js"></script>
<!-- bootstrap color picker -->
<script src="/static/plugins/colorpicker/bootstrap-colorpicker.min.js"></script>
<!-- bootstrap time picker -->
<script src="/static/plugins/timepicker/bootstrap-timepicker.min.js"></script>
<!-- SlimScroll 1.3.0 -->
<script src="/static/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- iCheck 1.0.1 -->
<script src="/static/plugins/iCheck/icheck.min.js"></script>
<!-- FastClick -->
<script src="/static/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="/static/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/static/dist/js/demo.js"></script>
<script>
    function modifyTask(id) {
        window.location.href = "/task/modify.do?taskId=" + id
    }
    function removeTask(id) {
        if (confirm("你确定要删除task-" + id + "吗？")) {
            window.location.href = "/task/remove.do?taskId=" + id
        }
    }
    function addTask() {
        window.location.href = "/task/publish.do"
    }
</script>
</body>
</html>
