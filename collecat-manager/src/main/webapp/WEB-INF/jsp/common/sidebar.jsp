<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">
            <%
                String selected = request.getParameter("selected");
                if ("task".equals(selected) || "job".equals(selected) || "node".equals(selected) || "DashBoard".equals(selected)) {
            %>
            <li class="treeview active">
                    <%
                } else{
            %>
            <li class="treeview">
                <%
                    }
                %>
                <a href="index.do">
                    <i class="glyphicon glyphicon-dashboard"></i> <span>DashBoard</span>
                </a>
                <ul class="treeview-menu">
                    <%
                        if ("task".equals(selected)) {
                    %>
                    <li class="active">
                            <%
                        } else {
                    %>
                    <li>
                        <%}%>
                        <a href="task.do"><i class="glyphicon glyphicon-list-alt"></i>Task监控管理
                            <p>
                                <small>查看与管理task状态</small>
                            </p>
                        </a>
                    </li>
                    <%
                        if ("job".equals(selected)) {
                    %>
                    <li class="active">
                            <%
                        } else {
                    %>
                    <li>
                        <%}%><a href="#"><i class="glyphicon glyphicon-list-alt"></i>Job监控与管理
                        <p>
                            <small>查看与管理job状态</small>
                        </p>
                    </a></li>
                    <%
                        if ("node".equals(selected)) {
                    %>
                    <li class="active">
                            <%
                        } else {
                    %>
                    <li>
                        <%}%>
                        <a href="#"><i class="glyphicon glyphicon-th"></i> Node监控
                            <p>
                                <small>查看node状态</small>
                            </p>
                        </a>
                    </li>

                </ul>
            </li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>