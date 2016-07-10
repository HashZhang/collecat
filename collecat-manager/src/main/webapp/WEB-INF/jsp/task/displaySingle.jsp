<%@ page import="com.sf.collecat.common.model.Subtask" %>
<%@ page import="com.sf.collecat.common.utils.StrUtils" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.sf.collecat.common.model.Task" %>
<%--
  Author： HashZhang
  Date: 2016/7/2
  Time: 11:41
--%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <title>Task display</title>
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
        <section class="content" style="overflow:scroll;height: inherit;">
            <div class="row">
                <label class="text-red"><b>
                    <%
                        String message = (String) request.getAttribute("message");
                        out.print(StrUtils.transferNull(message));
                    %>
                </b></label>
            </div>
            <div class="box box-primary">
                <div class="box-header">
                    <%
                        Task task = (Task) request.getAttribute("subtask");
                        out.println(StrUtils.makeString("<h3 class=\"box-title\"> TASK-", task.getId(), "</h3>"));
                    %>

                </div>
                <div class="box-body">
                    <div class="col-lg-2 col-sm-2">
                        <button onclick="startTask()" type="button" class="btn btn-block btn-success" <%
                            if (task.getIsActive()) {
                                out.print("disabled");
                            }
                        %>>开启
                        </button>
                    </div>
                    <div class="col-lg-2 col-sm-2">
                        <button onclick="stopTask()" type="button" class="btn btn-block btn-danger"<%
                            if (!task.getIsActive()) {
                                out.print("disabled");
                            }
                        %>>关闭
                        </button>
                    </div>
                    <div class="col-lg-2 col-sm-2">
                        <button onclick="modifyTask()" type="button" class="btn btn-block btn-primary">修改</button>
                    </div>
                    <div class="col-lg-2 col-sm-2">
                        <button onclick="removeTask()" type="button" class="btn btn-block btn-primary">删除</button>
                    </div>
                    <table id="task" class="table table-bordered table-striped" cellspacing="1" width="100%">
                        <thead>
                        </thead>
                        <tfoot>
                        </tfoot>
                        <tbody>
                        <tr>
                            <td>Schema Used:</td>
                            <td><%out.print(task.getSchemaUsed());%></td>
                        </tr>
                        <tr>
                            <td>Initial SQL:</td>
                            <td><%out.print(task.getInitialSql());%></td>
                        </tr>
                        <tr>
                            <td>Time Field:</td>
                            <td><%out.print(task.getTimeField());%></td>
                        </tr>
                        <tr>
                            <td>Routine Time:</td>
                            <td><%out.print(task.getRoutineTime());%></td>
                        </tr>
                        <tr>
                            <td>Allocate Routine:</td>
                            <td><%out.print(task.getAllocateRoutine());%></td>
                        </tr>
                        <tr>
                            <td>Active?:</td>
                            <td><%out.print(task.getIsActive());%></td>
                        </tr>

                        <tr>
                            <td>Current Time Complete percent:</td>
                            <td><div class="col-xs-6 col-md-3 text-center">
                                <div style="display: inline; width: 90px; height: 90px;">
                                    <input type="text" class="knob" value="<%
                                        out.print(task.getCurrentCompPer());
                                    %>" data-width="90" data-height="90" readOnly="true" data-fgcolor="#3c8dbc" style="width: 49px; height: 30px; position: absolute; vertical-align: middle; margin-top: 30px; margin-left: -69px; border: 0px; font-weight: bold; font-style: normal; font-variant: normal; font-stretch: normal; font-size: 18px; line-height: normal; font-family: Arial; text-align: center; color: rgb(60, 141, 188); padding: 0px; -webkit-appearance: none; background: none;">
                                </div>
                            </div></td>
                        </tr>
                        <tr>
                            <td>Total Time Complete percent:</td>
                            <td><div class="col-xs-6 col-md-3 text-center">
                                <div style="display: inline; width: 90px; height: 90px;">
                                    <input type="text" class="knob" value="<%
                                        out.print(task.getTotalCompPer());
                                    %>" data-width="90" data-height="90" readOnly="true" data-fgcolor="#3c8dbc" style="width: 49px; height: 30px; position: absolute; vertical-align: middle; margin-top: 30px; margin-left: -69px; border: 0px; font-weight: bold; font-style: normal; font-variant: normal; font-stretch: normal; font-size: 18px; line-height: normal; font-family: Arial; text-align: center; color: rgb(60, 141, 188); padding: 0px; -webkit-appearance: none; background: none;">
                                </div>
                            </div></td>
                        </tr>

                        <tr>
                            <td>Last Time:</td>
                            <td><%out.print(subtask.getLastTime());%></td>
                        </tr>
                        <tr>
                            <td>End Time:</td>
                            <td><%out.print(subtask.getEndTime());%></td>
                        </tr>
                        <tr>
                            <td>Active?:</td>
                            <td><%out.print(subtask.getIsActive());%></td>
                        </tr>
                        <tr>
                            <td>MySQL URL:</td>
                            <td><%out.print(subtask.getMysqlUrl());%></td>
                        </tr>
                        <tr>
                            <td>MySQL USERNAME:</td>
                            <td><%out.print(subtask.getMysqlUsername());%></td>
                        </tr>
                        <tr>
                            <td>MYSQL Password:</td>
                            <td><%out.print(subtask.getMysqlPassword());%></td>
                        </tr>
                        <tr>
                            <td>Kafka Cluster Name:</td>
                            <td><%out.print(subtask.getKafkaClusterName());%></td>
                        </tr>
                        <tr>
                            <td>Kafka Url:</td>
                            <td><%out.print(subtask.getKafkaUrl());%></td>
                        </tr>
                        <tr>
                            <td>Kafka Topic:</td>
                            <td><%out.print(subtask.getKafkaTopic());%></td>
                        </tr>
                        <tr>
                            <td>Kafka Topic Tokens:</td>
                            <td><%out.print(subtask.getKafkaTopicTokens());%></td>
                        </tr>
                        <tr>
                            <td>Kafka Message size:</td>
                            <td><%out.print(subtask.getKafkaMessageSize());%></td>
                        </tr>
                        <tr>
                            <td>Message Format:</td>
                            <td><%out.print(subtask.getMessageFormat());%></td>
                        </tr>

                        </tbody>
                    </table>
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
<!-- SlimScroll 1.3.0 -->
<script src="/collecat-manager/static/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="/collecat-manager/static/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="/collecat-manager/static/dist/js/app.min.js"></script>
<!-- AdminLTE for demo purposes -->
<script src="/collecat-manager/static/dist/js/demo.js"></script>
<!-- jQuery Knob -->
<script src="/collecat-manager/static/plugins/knob/jquery.knob.js"></script>
<!-- Sparkline -->
<script src="/collecat-manager/static/plugins/sparkline/jquery.sparkline.min.js"></script>

<script>
    $(function () {
        /* jQueryKnob */

        $(".knob").knob({
            /*change : function (value) {
             //console.log("change : " + value);
             },
             release : function (value) {
             console.log("release : " + value);
             },
             cancel : function () {
             console.log("cancel : " + this.value);
             },*/
            draw: function () {

                // "tron" case
                if (this.$.data('skin') == 'tron') {

                    var a = this.angle(this.cv)  // Angle
                            , sa = this.startAngle          // Previous start angle
                            , sat = this.startAngle         // Start angle
                            , ea                            // Previous end angle
                            , eat = sat + a                 // End angle
                            , r = true;

                    this.g.lineWidth = this.lineWidth;

                    this.o.cursor
                    && (sat = eat - 0.3)
                    && (eat = eat + 0.3);

                    if (this.o.displayPrevious) {
                        ea = this.startAngle + this.angle(this.value);
                        this.o.cursor
                        && (sa = ea - 0.3)
                        && (ea = ea + 0.3);
                        this.g.beginPath();
                        this.g.strokeStyle = this.previousColor;
                        this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, sa, ea, false);
                        this.g.stroke();
                    }

                    this.g.beginPath();
                    this.g.strokeStyle = r ? this.o.fgColor : this.fgColor;
                    this.g.arc(this.xy, this.xy, this.radius - this.lineWidth, sat, eat, false);
                    this.g.stroke();

                    this.g.lineWidth = 2;
                    this.g.beginPath();
                    this.g.strokeStyle = this.o.fgColor;
                    this.g.arc(this.xy, this.xy, this.radius - this.lineWidth + 1 + this.lineWidth * 2 / 3, 0, 2 * Math.PI, false);
                    this.g.stroke();

                    return false;
                }
            }
        });
        /* END JQUERY KNOB */

        //INITIALIZE SPARKLINE CHARTS
        $(".sparkline").each(function () {
            var $this = $(this);
            $this.sparkline('html', $this.data());
        });

        /* SPARKLINE DOCUMENTATION EXAMPLES http://omnipotent.net/jquery.sparkline/#s-about */
        drawDocSparklines();
        drawMouseSpeedDemo();

    });

</script>


</body>
</html>