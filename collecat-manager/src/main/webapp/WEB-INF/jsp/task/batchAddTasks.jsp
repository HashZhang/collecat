<%@ page import="com.sf.collecat.common.utils.StrUtils" %><%--
  Author： HashZhang
  Date: 2016/7/4
  Time: 11:41
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>Modify Task</title>
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
                Batch Add Task
                <small></small>
            </h1>
            <ol class="breadcrumb">
                <li><a href="/collecat-manager/index.do"><i class="glyphicon glyphicon-home"></i>Home</a></li>
                <li><a href="/collecat-manager/task.do">Task</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content" style="overflow:scroll;height: inherit;">
            <%
                String message = (String) request.getAttribute("message");
                String taskProperties = (String) request.getAttribute("tasksProperties");
                out.print("<h2 class=\"text-red\">"+ StrUtils.transferNull(message)+"</h2>");
                out.print("<div class=\"box box-primary\">\n" +
                        "                <div class=\"box-header\">\n" +
                        "                    <h3 class=\"box-title\">#批量添加，请输入tasks.properties内容：</h3>\n" +
                        "                </div>\n" +
                        "                <div class=\"box-body pad\">" +
                        "                   <form role=\"form\" method=\"post\" action=\"\\collecat-manager\\task\\batchAdd.do\">\n" +
                        "                   <textarea id=\"tasksProperties\" name=\"tasksProperties\" rows=\"20\" cols=\"80\">\n" +
                        StrUtils.transferNull(taskProperties) +
                        "                   </textarea>\n" +
                        "                           <input type=\"submit\" value=\"提交\"/>\n" +
                        "                   <input type=\"reset\" value=\"重置\"/>\n" +
                        "                   </form>" +
                        "               </div></div>");
            %>

            <div class="box box-primary">
                <div class="box-header">
                    <h3 class="box-title">#示例：</h3>
                </div>
                <div class="box-body">
                    <p class="text-gray">#填写所有task的名字，以逗号分隔</p>
                    <p>tasks=task1,task2</p>
                    <p class="text-gray">#所在MyCat的schema</p>
                    <p>task1.schema.used = exp</p>
                    <p class="text-gray">#原始语句,语句只能有一句并且只包含一张表，可以有任意的条件和其他元素</p>
                    <p>task1.initial.sql = select * from tt_receive_order</p>
                    <p class="text-gray">#是否执行这个数据导出工作</p>
                    <p>task1.is.active = true</p>
                    <p class="text-gray">#kafka配置</p>
                    <p>task1.kafka.url = http://mommon_other.intsit.sfdc.com.cn:1080/mom-mon/monitor/requestService.pub</p>
                    <p>task1.kafka.topic = SGS_TT_REC_ORD</p>
                    <p>task1.kafka.topic.token = SGS_TT_REC_ORD:0vqT!*eT</p>
                    <p>task1.kafka.cluster.name = other</p>
                    <p class="text-gray">#每个kafka消息存放多少条结果</p>
                    <p>task1.kafka.message.size = 1000</p>
                    <p class="text-gray">#表中作为依据的时间字段</p>
                    <p>task1.time.field = modify_tm</p>
                    <p class="text-gray">#从哪个时间开始抽取</p>
                    <p>task1.last.time = 2016-6-24 12:10:00</p>
                    <p class="text-gray">#每次从数据库中取出多长时间的记录（单位：秒），即默认从开始时间到目前时间，每次去下面时间长度的记录量</p>
                    <p>task1.routine.time = 600</p>
                    <p class="text-gray">#cron表达式</p>
                    <p>task1.allocate.routine = 05 15 * * *</p>
                    <p class="text-gray">#写入kafka的数据格式，有csv和json两种</p>
                    <p>task1.message.format = csv</p>
                    <p></p>
                    <p>task2.schema.used = exp</p>
                    <p>task2.initial.sql = select * from tt_delivery_order</p>
                    <p>task2.is.active = true</p>
                    <p>task2.kafka.url = http://mommon_other.intsit.sfdc.com.cn:1080/mom-mon/monitor/requestService.pub</p>
                    <p>task2.kafka.topic = SGS_TT_DEL_ORD</p>
                    <p>task2.kafka.topic.token = SGS_TT_DEL_ORD:6o2M6V^5</p>
                    <p>task2.kafka.message.size = 1000</p>
                    <p>task2.kafka.cluster.name = other</p>
                    <p>task2.time.field = modified_tm</p>
                    <p>task2.last.time = 2016-6-24 12:10:00</p>
                    <p>task2.routine.time = 600</p>
                    <p>task2.allocate.routine = 05 15 * * *</p>
                    <p>task2.message.format = csv</p>
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