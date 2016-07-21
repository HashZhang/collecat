<%@ page import="com.sf.collecat.common.model.Task" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sf.collecat.common.utils.StrUtils" %>
<%@ page import="com.sf.collecat.common.model.Subtask" %><%--
  Author： HashZhang
  Date: 2016/7/2
  Time: 11:41
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>Modify Subtask</title>
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
                Subtask
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/collecat-manager/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/collecat-manager/task.do">Task</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content" style="overflow:scroll;height: inherit;">
            <div class="row">
                <label class="text-red"><b>
                    <%
                        String message = (String) request.getAttribute("message");
                        out.print(StrUtils.transferNull(message));
                    %>
                </b></label>
            </div>
            <%
                SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat formatter2 = new SimpleDateFormat("HH:mm");
                Subtask subtask = (Subtask) request.getAttribute("subtask");

                out.print("<form role=\"form\" method=\"post\" action=\"\\collecat-manager\\subtask\\update.do?subtaskId="+ subtask.getId()+ "\">");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Initial SQL:(抽取表的初始SQL，决定如何筛选出需要的数据的SQL语句)</label>\n" +
                        "       <input id=\"initialSql\" name=\"initialSql\" type=\"text\" class=\"form-control\" value=\"" + subtask.getInitialSql() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Schema Used:(位于MyCat的哪个逻辑库schema上)</label>\n" +
                        "       <input id=\"schemaUsed\" name=\"schemaUsed\" type=\"text\" class=\"form-control\" value=\"" + subtask.getSchemaUsed() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Time Field0:(抽取表根据的时间字段)</label>\n" +
                        "       <input id=\"timeField\" name=\"timeField\" type=\"text\" class=\"form-control\" value=\"" + subtask.getTimeField() + "\">\n" +
                        "   </div>");

                out.print("<div class=\"form-group\">\n" +
                        "                <label>Last Time(最近抽取日期):</label>\n" +
                        "\n" +
                        "                <div class=\"input-group date\">\n" +
                        "                  <div class=\"input-group-addon\">\n" +
                        "                    <i class=\"fa fa-calendar\"></i>\n" +
                        "                  </div>\n" +
                        "                  <input type=\"text\" class=\"form-control pull-right\" id=\"lastDate\" name=\"lastDate\" value=\"" +
                        (subtask.getLastTime() == null ? "" : formatter1.format(subtask.getLastTime())) +
                        "                   \">\n" +
                        "                </div>\n" +
                        "                <!-- /.input group -->\n" +
                        "              </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Last Time(最近抽取时间):<</label>\n" +
                        "       <input id=\"lastTime\" name=\"lastTime\" type=\"text\" class=\"form-control\" value=\"" + (subtask.getLastTime() == null ? "" : formatter2.format(subtask.getLastTime())) + "\">\n" +
                        "   </div>");
                out.print("<div class=\"form-group\">\n" +
                        "                <label>Last Time(抽取的结束日期，留空则为无限期):</label>\n" +
                        "\n" +
                        "                <div class=\"input-group date\">\n" +
                        "                  <div class=\"input-group-addon\">\n" +
                        "                    <i class=\"fa fa-calendar\"></i>\n" +
                        "                  </div>\n" +
                        "                  <input type=\"text\" class=\"form-control pull-right\" id=\"endDate\" name=\"endDate\" value=\"" +
                        (subtask.getEndTime() == null ? "" : formatter1.format(subtask.getEndTime())) +
                        "                   \">\n" +
                        "                </div>\n" +
                        "                <!-- /.input group -->\n" +
                        "              </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Last Time(抽取的结束时间):<</label>\n" +
                        "       <input id=\"endTime\" name=\"endTime\" type=\"text\" class=\"form-control\" value=\"" + (subtask.getEndTime() == null ? "" : formatter2.format(subtask.getEndTime())) + "\">\n" +
                        "   </div>");

                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Routine Time(单位：秒):(每个job抽取时间长度)</label>\n" +
                        "       <input id=\"routineTime\" name=\"routineTime\" type=\"text\" class=\"form-control\" value=\"" + subtask.getRoutineTime() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Allocate Routine:(调度cron表达式，决定多久让task生成一次job)</label>\n" +
                        "       <input id=\"allocateRoutine\" name=\"allocateRoutine\" type=\"text\" class=\"form-control\" value=\"" + subtask.getAllocateRoutine() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>" +
                        "           <input type=\"checkbox\" value=\"1\" name=\"active\"");
                if (subtask.getIsActive()) {
                    out.print("checked");
                }
                out.print(">active(这个subtask是否处于活跃状态，如果非活跃状态，不会生成job)" +
                        "           <input type=\"hidden\" name=\"active\"/>" +
                        "       </label>" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>MySQL URL:</label>\n" +
                        "       <input id=\"mysqlUrl\" name=\"mysqlUrl\" type=\"text\" class=\"form-control\" value=\"" + subtask.getMysqlUrl() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>MySQL Username:</label>\n" +
                        "       <input id=\"mysqlUsername\" name=\"mysqlUsername\" type=\"text\" class=\"form-control\" value=\"" + subtask.getMysqlUsername() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>MySQL Password:</label>\n" +
                        "       <input id=\"mysqlPassword\" name=\"mysqlPassword\" type=\"text\" class=\"form-control\" value=\"" + subtask.getMysqlPassword() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Kafka Topic:</label>\n" +
                        "       <input id=\"kafkaTopic\" name=\"kafkaTopic\" type=\"text\" class=\"form-control\" value=\"" + subtask.getKafkaTopic() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Kafka Url:</label>\n" +
                        "       <input id=\"kafkaUrl\" name=\"kafkaUrl\" type=\"text\" class=\"form-control\" value=\"" + subtask.getKafkaUrl() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Kafka Cluster Name:</label>\n" +
                        "       <input id=\"kafkaClusterName\" name=\"kafkaClusterName\" type=\"text\" class=\"form-control\" value=\"" + subtask.getKafkaClusterName() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Kafka Topic Tokens:</label>\n" +
                        "       <input id=\"kafkaTopicTokens\" name=\"kafkaTopicTokens\" type=\"text\" class=\"form-control\" value=\"" + subtask.getKafkaTopicTokens() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Kafka Message Size:(抽取到的kafka每条message最多包含多少条记录)</label>\n" +
                        "       <input id=\"kafkaMessageSize\" name=\"kafkaMessageSize\" type=\"text\" class=\"form-control\" value=\"" + subtask.getKafkaMessageSize() + "\">\n" +
                        "   </div>");
                out.print(" <div class=\"form-group\">\n" +
                        "       <label>Message Format:(数据格式，目前支持csv和json)</label>\n" +
                        "       <input id=\"messageFormat\" name=\"messageFormat\" type=\"text\" class=\"form-control\" value=\"" + subtask.getMessageFormat() + "\">\n" +
                        "   </div>");
                out.print(" <input type=\"submit\" value=\"提交\" />");
                out.print(" <input type=\"reset\" value=\"重置\" />");
                out.print("</form>");
            %>
        </section>
        <jsp:include page="../common/footer.jsp"></jsp:include>
    </div>
</div>
<!-- jQuery 2.2.0 -->
<script src="/collecat-manager/static/plugins/jQuery/jQuery-2.2.0.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="/collecat-manager/static/bootstrap/js/bootstrap.min.js"></script>
<!-- Select2 -->
<script src="/collecat-manager/static/plugins/select2/select2.full.min.js"></script>
<!-- InputMask -->
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/collecat-manager/static/plugins/input-mask/jquery.inputmask.extensions.js"></script>
<!-- date-range-picker -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.11.2/moment.min.js"></script>
<script src="/collecat-manager/static/plugins/daterangepicker/daterangepicker.js"></script>
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
<script>
    $(document).ready(function () {
        //Initialize Select2 Elements
        $(".select2").select2();

        //Datemask dd/mm/yyyy
        $("#datemask").inputmask("dd/mm/yyyy", {"placeholder": "dd/mm/yyyy"});
        //Datemask2 mm/dd/yyyy
        $("#datemask2").inputmask("mm/dd/yyyy", {"placeholder": "mm/dd/yyyy"});
        //Money Euro
        $("[data-mask]").inputmask();

        //Date range picker
        $('#reservation').daterangepicker();
        //Date range picker with time picker
        $('#reservationtime').daterangepicker({timePicker: true, timePickerIncrement: 30, format: 'MM/DD/YYYY h:mm A'});
        //Date range as a button
        $('#daterange-btn').daterangepicker(
                {
                    ranges: {
                        'Today': [moment(), moment()],
                        'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                        'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                        'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                        'This Month': [moment().startOf('month'), moment().endOf('month')],
                        'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
                    },
                    startDate: moment().subtract(29, 'days'),
                    endDate: moment()
                },
                function (start, end) {
                    $('#daterange-btn span').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));
                }
        );

        //Date picker
        $('#lastdate').datepicker({
            autoclose: true
        });
        $('#endDate').datepicker({
            autoclose: true
        });
        //iCheck for checkbox and radio inputs
        $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
            checkboxClass: 'icheckbox_minimal-blue',
            radioClass: 'iradio_minimal-blue'
        });
        //Red color scheme for iCheck
        $('input[type="checkbox"].minimal-red, input[type="radio"].minimal-red').iCheck({
            checkboxClass: 'icheckbox_minimal-red',
            radioClass: 'iradio_minimal-red'
        });
        //Flat red color scheme for iCheck
        $('input[type="checkbox"].flat-red, input[type="radio"].flat-red').iCheck({
            checkboxClass: 'icheckbox_flat-green',
            radioClass: 'iradio_flat-green'
        });

        //Colorpicker
        $(".my-colorpicker1").colorpicker();
        //color picker with addon
        $(".my-colorpicker2").colorpicker();

        //Timepicker
        $(".timepicker").timepicker({
            showInputs: false
        });
    });
</script>
</body>
</html>