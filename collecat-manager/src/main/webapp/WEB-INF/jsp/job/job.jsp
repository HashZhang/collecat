<%@ page import="com.sf.collecat.common.Constants" %>
<%@ page import="com.sf.collecat.common.model.Job" %>
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
    <title>Node</title>
    <jsp:include page="../common/headInclude.jsp"></jsp:include>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <jsp:include page="../common/header.jsp"></jsp:include>
    <jsp:include page="../common/sidebar.jsp">
        <jsp:param name="selected" value="job"/>
    </jsp:include>
    <div class="content-wrapper">

        <section class="content-header">
            <h1>
                Job
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/collecat-manager/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/collecat-manager/job.do">Job</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content" >
            <div class="row">
                <label class="text-red"><b>
                    <%
                        String message = (String) request.getAttribute("message");
                        out.print(StrUtils.transferNull(message));
                    %>
                </b></label>
            </div>
            <div class="box box-primary" style="overflow:scroll;height:inherit">
                <div class="box-header">
                    <h3 class="box-title"><b>所有Jobs：</b></h3>
                </div>
                <div class="box-body">
                    <%
                        SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        List<Job> jobs = (List<Job>) request.getAttribute("allJobs");
                        out.print("<table id=\"allJobs\" class=\"table table-bordered table-striped\" cellspacing=\"0\" width=\"100%\">\n" +
                                "        <thead>\n" +
                                "            <tr>\n" +
                                "                <th>  </th>\n" +
                                "                <th>ID</th>\n" +
                                "                <th>TIME FIELD</th>\n" +
                                "                <th>TIME FIELD START</th>\n" +
                                "                <th>TIME FIELD END</th>\n" +
                                "                <th>JOB SQL</th>\n" +
                                "                <th>MYSQL URL</th>\n" +
                                "                <th>MYSQL USERNAME</th>\n" +
                                "                <th>MYSQL PASSWORD</th>\n" +
                                "                <th>CREATED TIME</th>\n" +
                                "                <th>MODIFIED TIME</th>\n" +
                                "                <th>KAFKA TOPIC</th>\n" +
                                "                <th>KAFKA URL</th>\n" +
                                "                <th>KAFKA CLUSTER NAME</th>\n" +
                                "                <th>KAFKA TOPIC TOKENS</th>\n" +
                                "                <th>KAFKA MESSAGE SIZE</th>\n" +
                                "                <th>MESSAGE FORMAT</th>\n" +
                                "                <th>STATUS</th>\n" +
                                "                <th>NODE ASSIGNED TO</th>\n" +
                                "            </tr>\n" +
                                "        </thead>\n" +
                                "        <tfoot>\n" +
                                "        </tfoot>\n" +
                                "        <tbody>\n");
                        for (Job job : jobs) {
                            out.print("<tr>" +
                                    "<td><button onclick=\"resetJob(" + job.getId() + ")\" type=\"button\" class=\"btn btn-block btn-primary\">重置</button>" +
                                    "<button onclick=\"removeJob(" + job.getId() + ")\" type=\"button\" class=\"btn btn-block btn-danger\">删除</button></td>" +
                                    "<td>" + job.getId() + "</td>" +
                                    "<td>" + job.getTimeField() + "</td>" +
                                    "<td>" + formatter1.format(job.getTimeFieldStart()) + "</td>" +
                                    "<td>" + formatter1.format(job.getTimeFieldEnd()) + "</td>" +
                                    "<td>" + job.getJobSql() + "</td>" +
                                    "<td>" + job.getMysqlUrl() + "</td>" +
                                    "<td>" + job.getMysqlUsername() + "</td>" +
                                    "<td>" + job.getMysqlPassword() + "</td>" +
                                    "<td>" + formatter1.format(job.getCreatedTime()) + "</td>" +
                                    "<td>" + formatter1.format(job.getModifiedTime()) + "</td>" +
                                    "<td>" + job.getKafkaTopic() + "</td>" +
                                    "<td>" + job.getKafkaUrl() + "</td>" +
                                    "<td>" + job.getKafkaClusterName() + "</td>" +
                                    "<td>" + job.getKafkaTopicTokens() + "</td>" +
                                    "<td>" + job.getKafkaMessageSize() + "</td>" +
                                    "<td>" + job.getMessageFormat() + "</td>");
                            String status;
                            switch (job.getStatus()) {
                                case Constants.JOB_INIT_VALUE:
                                    status = "工作中";
                                    break;
                                case Constants.JOB_FINISHED_VALUE:
                                    status = "已完成";
                                    break;
                                case Constants.JOB_EXCEPTION_VALUE:
                                default:
                                    status = "有异常";
                            }

                            out.print(
                                    "<td>" + status + "</td>" +
                                            "<td>" + job.getNodeAssignedTo() + "</td>" +
                                            "</tr>");
                        }
                        out.print("        </tbody>\n" +
                                "    </table>");
                    %>
                </div>
            </div>
            <div class="box box-primary">
                <div class="box-header">
                    <h3 class="box-title">#注释：</h3>
                </div>
                <div class="box-body">
                    <ol>
                        <li>TIME FIELD: 抽取表根据的时间字段</li>
                        <li>TIME FIELD START: 本次Job时间字段起始时间</li>
                        <li>TIME FIELD END: 本次Job时间字段终止时间</li>
                        <li>JOB SQL: 本次Job执行的SQL</li>
                        <li>MYSQL URL: 本次Job抽取的MySQL URL</li>
                        <li>MYSQL USERNAME: 本次Job抽取的MySQL用户名</li>
                        <li>MYSQL PASSWORD: 本次Job抽取的MySQL密码</li>
                        <li>Kafka Topic:抽取到的kafka topic</li>
                        <li>Kafka Url:抽取到的kafka url</li>
                        <li>Kafka Cluster Name:抽取到的kafka Cluster Name</li>
                        <li>Kafka Topic Tokens:抽取到的kafka token</li>
                        <li>Kafka Message Size:抽取到的kafka每条message最多包含多少条记录</li>
                        <li>Message Format:数据格式，目前支持csv和json</li>
                        <li>STATUS:Job状态</li>
                        <li>NODE ASSIGNED TO:分配的Node编号</li>
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
