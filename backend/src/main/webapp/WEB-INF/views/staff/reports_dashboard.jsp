<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.example.erp.model.Student"%>
<%@ page import="com.example.erp.model.Notification"%>
<%@ page import="com.example.erp.model.Report"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartAttend ERP - Academic Reports Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col">

    <!-- Header Navigation -->
    <header class="bg-slate-900 text-white shadow-md">
        <div class="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
            <div class="flex items-center space-x-3">
                <span class="text-2xl font-bold tracking-tight bg-gradient-to-r from-cyan-400 to-indigo-200 bg-clip-text text-transparent">SmartAttend ERP</span>
                <span class="bg-slate-800 text-xs text-slate-300 font-semibold px-2.5 py-1 rounded-full border border-slate-700">Faculty Reports Portal</span>
            </div>
            <div class="flex items-center space-x-4">
                <a href="${pageContext.request.contextPath}/staff/marks" class="text-sm font-medium text-slate-300 hover:text-white transition">Marks Module</a>
                <a href="${pageContext.request.contextPath}/logout" class="bg-indigo-600 hover:bg-indigo-700 text-xs font-semibold px-4 py-2 rounded-lg transition shadow">Sign Out</a>
            </div>
        </div>
    </header>

    <%
        Map<String, Object> dashboardStats = (Map<String, Object>) request.getAttribute("dashboardStats");
        int totalStudents = 0;
        int totalStaff = 0;
        int presentToday = 0;
        int absentToday = 0;
        double averageAttendance = 0.0;
        String latestDate = "No Records";
        int goodStandingCount = 0;
        int atRiskCount = 0;
        
        if (dashboardStats != null) {
            totalStudents = (Integer) dashboardStats.getOrDefault("totalStudents", 0);
            totalStaff = (Integer) dashboardStats.getOrDefault("totalStaff", 0);
            presentToday = (Integer) dashboardStats.getOrDefault("presentToday", 0);
            absentToday = (Integer) dashboardStats.getOrDefault("absentToday", 0);
            averageAttendance = (Double) dashboardStats.getOrDefault("averageAttendance", 0.0);
            latestDate = (String) dashboardStats.getOrDefault("latestDate", "No Records");
            goodStandingCount = (Integer) dashboardStats.getOrDefault("goodStandingCount", 0);
            atRiskCount = (Integer) dashboardStats.getOrDefault("atRiskCount", 0);
        }
        
        List<Notification> notificationsList = (List<Notification>) request.getAttribute("notifications");
        List<Report> recentReportsList = (List<Report>) request.getAttribute("recentActivities");
    %>

    <!-- Content Workspace -->
    <main class="flex-grow max-w-7xl w-full mx-auto px-4 py-8 space-y-8">
        
        <!-- Header Introduction -->
        <div class="flex flex-col md:flex-row md:items-center md:justify-between border-b border-slate-200 pb-6">
            <div>
                <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">Academic Reports Dashboard</h1>
                <p class="text-slate-500 mt-1">Deploy, verify, print and export multi-dimensional attendance and evaluation metrics.</p>
            </div>
            <div class="mt-4 md:mt-0 bg-slate-900 text-white rounded-lg px-4 py-2.5 flex items-center space-x-2 shadow-sm text-xs font-medium">
                <svg class="w-4 h-4 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                <span>Active Date Focus: <%= latestDate %></span>
            </div>
        </div>

        <%
            String error = request.getParameter("error");
            if (error != null) {
        %>
            <div class="bg-rose-50 border-l-4 border-rose-500 text-rose-800 p-4 rounded-r-lg shadow-sm flex items-center space-x-3">
                <svg class="w-5 h-5 text-rose-500" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"></path></svg>
                <span class="font-medium">Error: <%= error %></span>
            </div>
        <% } %>

        <!-- ==================== DASHBOARD STATS CARDS ==================== -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
            <!-- Card 1: Total Students -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between">
                <div>
                    <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Total Students</span>
                    <span class="text-2xl font-extrabold text-slate-800 block mt-1"><%= totalStudents %></span>
                    <span class="text-[10px] text-emerald-600 font-medium flex items-center mt-1">
                        <svg class="w-3.5 h-3.5 mr-0.5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M12 7a1 1 0 110-2h5a1 1 0 011 1v5a1 1 0 11-2 0V8.414l-4.293 4.293a1 1 0 01-1.414 0L8 10.414l-4.293 4.293a1 1 0 01-1.414-1.414l5-5a1 1 0 011.414 0L10 10.586 13.586 7H12z" clip-rule="evenodd"></path></svg>
                        In Good Standing
                    </span>
                </div>
                <div class="bg-indigo-50 text-indigo-600 p-3.5 rounded-lg">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
                </div>
            </div>

            <!-- Card 2: Total Staff -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between">
                <div>
                    <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Total Staff</span>
                    <span class="text-2xl font-extrabold text-slate-800 block mt-1"><%= totalStaff %></span>
                    <span class="text-[10px] text-slate-400 font-medium block mt-1">Faculty Registry</span>
                </div>
                <div class="bg-cyan-50 text-cyan-600 p-3.5 rounded-lg">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 13.255A23.931 23.931 0 0112 15c-3.183 0-6.22-.62-9-1.745M16 6V4a2 2 0 00-2-2h-4a2 2 0 00-2 2v2m4 6h.01M5 20h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                </div>
            </div>

            <!-- Card 3: Present Today -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between">
                <div>
                    <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Present Today</span>
                    <span class="text-2xl font-extrabold text-emerald-600 block mt-1"><%= presentToday %></span>
                    <span class="text-[10px] text-slate-400 font-medium block mt-1">Marked Present</span>
                </div>
                <div class="bg-emerald-50 text-emerald-600 p-3.5 rounded-lg">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
            </div>

            <!-- Card 4: Absent Today -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between">
                <div>
                    <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Absent Today</span>
                    <span class="text-2xl font-extrabold text-rose-600 block mt-1"><%= absentToday %></span>
                    <span class="text-[10px] text-slate-400 font-medium block mt-1">Marked Absent</span>
                </div>
                <div class="bg-rose-50 text-rose-600 p-3.5 rounded-lg">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                </div>
            </div>

            <!-- Card 5: Average Attendance -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 flex items-center justify-between">
                <div>
                    <span class="text-xs font-bold text-slate-400 uppercase tracking-wider block">Average Attendance</span>
                    <span class="text-2xl font-extrabold text-indigo-600 block mt-1"><%= averageAttendance %>%</span>
                    <span class="text-[10px] text-slate-400 font-medium block mt-1">Overall aggregate</span>
                </div>
                <div class="bg-indigo-50 text-indigo-600 p-3.5 rounded-lg">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z"></path></svg>
                </div>
            </div>
        </div>

        <!-- ==================== CHARTS ANALYTICS GRID ==================== -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <!-- Weekly Attendance Trends (Line Chart) -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col justify-between">
                <div class="mb-4">
                    <h3 class="text-base font-bold text-slate-800">Weekly Attendance Trends</h3>
                    <p class="text-xs text-slate-400">Class presence progress over time</p>
                </div>
                <div class="relative h-64">
                    <canvas id="weeklyTrendsChart"></canvas>
                </div>
            </div>

            <!-- Subject Wise Class Comparison (Bar Chart) -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col justify-between">
                <div class="mb-4">
                    <h3 class="text-base font-bold text-slate-800">Subject Wise Class Comparison</h3>
                    <p class="text-xs text-slate-400">Attendance distribution per academic course</p>
                </div>
                <div class="relative h-64">
                    <canvas id="subjectComparisonChart"></canvas>
                </div>
            </div>

            <!-- Student Standing (Pie Chart) -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col justify-between">
                <div class="mb-4">
                    <h3 class="text-base font-bold text-slate-800">Student Standings Breakdown</h3>
                    <p class="text-xs text-slate-400">Proportion of At-Risk (<75%) vs Good Standing students</p>
                </div>
                <div class="relative h-64 flex justify-center items-center">
                    <canvas id="standingChart"></canvas>
                </div>
            </div>
        </div>

        <!-- ==================== RECENT ACTIVITY & NOTIFICATIONS ==================== -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- Recent Activity Panel -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col h-[380px]">
                <div class="border-b border-slate-100 pb-3 mb-4 flex justify-between items-center">
                    <h3 class="text-base font-bold text-slate-800 flex items-center space-x-2">
                        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                        <span>Recent Activities</span>
                    </h3>
                    <span class="bg-slate-100 text-slate-500 text-[10px] font-bold px-2.5 py-1 rounded-full uppercase">Audit log</span>
                </div>
                <div class="overflow-y-auto flex-grow space-y-3 pr-2">
                    <%
                        if (recentReportsList != null && !recentReportsList.isEmpty()) {
                            for (Report rep : recentReportsList) {
                    %>
                        <div class="flex items-start space-x-3 p-3 bg-slate-50 rounded-lg hover:bg-indigo-50/20 transition">
                            <div class="bg-indigo-100 text-indigo-600 p-2 rounded-lg mt-0.5">
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                            </div>
                            <div class="flex-grow">
                                <span class="text-xs font-semibold text-slate-700 block"><%= rep.getTitle() %></span>
                                <p class="text-xs text-slate-400 mt-0.5"><%= rep.getDescription() %></p>
                                <span class="text-[10px] text-indigo-500 font-semibold block mt-1">Generated by <%= rep.getGeneratedBy() %></span>
                            </div>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <div class="text-center py-10 text-slate-400">
                            <p class="text-sm font-medium">No recent operations performed.</p>
                        </div>
                    <% } %>
                </div>
            </div>

            <!-- Notifications / Announcements Panel -->
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 flex flex-col h-[380px]">
                <div class="border-b border-slate-100 pb-3 mb-4 flex justify-between items-center">
                    <h3 class="text-base font-bold text-slate-800 flex items-center space-x-2">
                        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path></svg>
                        <span>Circular Announcements</span>
                    </h3>
                    <span class="bg-indigo-600 text-white text-[10px] font-bold px-2.5 py-1 rounded-full uppercase">Broadcast</span>
                </div>
                <div class="overflow-y-auto flex-grow space-y-3 pr-2">
                    <%
                        if (notificationsList != null && !notificationsList.isEmpty()) {
                            for (Notification note : notificationsList) {
                                String colorClass = "bg-slate-100 text-slate-600";
                                if ("ADMIN".equals(note.getSenderRole())) colorClass = "bg-amber-100 text-amber-800 border-l-4 border-amber-500";
                                else if ("SYSTEM".equals(note.getSenderRole())) colorClass = "bg-indigo-100 text-indigo-800 border-l-4 border-indigo-500";
                    %>
                        <div class="p-3.5 rounded-lg border border-slate-200 hover:border-indigo-100 transition <%= colorClass %>">
                            <div class="flex justify-between items-center">
                                <span class="text-xs font-bold uppercase tracking-wider"><%= note.getSenderRole() %></span>
                                <span class="text-[10px] text-slate-400 font-semibold"><%= note.getCreatedAt() %></span>
                            </div>
                            <span class="text-xs font-semibold block mt-1 text-slate-800"><%= note.getTitle() %></span>
                            <p class="text-xs text-slate-500 mt-1"><%= note.getMessage() %></p>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <div class="text-center py-10 text-slate-400">
                            <p class="text-sm font-medium">No recent active notifications.</p>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- ==================== CORE OPERATIONAL GRID (REPORTS GENERATION) ==================== -->
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            
            <!-- Left Column: Static & Profile Reports -->
            <div class="lg:col-span-1 space-y-6">
                <!-- Aggregate Reports -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <h2 class="text-lg font-bold text-slate-800 mb-4 flex items-center space-x-2">
                        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 002 2h2a2 2 0 002-2"></path></svg>
                        <span>Aggregate Reports</span>
                    </h2>
                    <p class="text-xs text-slate-400 mb-6 font-medium">Generate institutional distributions and high-level performance metrics.</p>

                    <div class="space-y-4">
                        <!-- Department Report Card Button -->
                        <a href="${pageContext.request.contextPath}/staff/reports?action=department" class="flex items-center justify-between p-3.5 bg-slate-50 hover:bg-indigo-50/50 rounded-lg border border-slate-200 hover:border-indigo-200 transition group">
                            <div class="flex items-center space-x-3">
                                <div class="bg-indigo-100 text-indigo-800 p-2 rounded group-hover:bg-indigo-200 transition">
                                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
                                </div>
                                <span class="text-sm font-semibold text-slate-700">Department Report</span>
                            </div>
                            <svg class="w-4 h-4 text-slate-400 group-hover:text-indigo-600 transition" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
                        </a>

                        <!-- Semester Report Card Button -->
                        <a href="${pageContext.request.contextPath}/staff/reports?action=semester" class="flex items-center justify-between p-3.5 bg-slate-50 hover:bg-indigo-50/50 rounded-lg border border-slate-200 hover:border-indigo-200 transition group">
                            <div class="flex items-center space-x-3">
                                <div class="bg-teal-100 text-teal-800 p-2 rounded group-hover:bg-teal-200 transition">
                                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
                                </div>
                                <span class="text-sm font-semibold text-slate-700">Semester Report</span>
                            </div>
                            <svg class="w-4 h-4 text-slate-400 group-hover:text-teal-600 transition" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
                        </a>

                        <!-- Overall College Report -->
                        <a href="${pageContext.request.contextPath}/staff/reports?action=overall" class="flex items-center justify-between p-3.5 bg-slate-50 hover:bg-indigo-50/50 rounded-lg border border-slate-200 hover:border-indigo-200 transition group">
                            <div class="flex items-center space-x-3">
                                <div class="bg-amber-100 text-amber-800 p-2 rounded group-hover:bg-amber-200 transition">
                                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"></path></svg>
                                </div>
                                <span class="text-sm font-semibold text-slate-700">Overall College Report</span>
                            </div>
                            <svg class="w-4 h-4 text-slate-400 group-hover:text-amber-600 transition" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path></svg>
                        </a>
                    </div>
                </div>

                <!-- Student Personal Card Search -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <h2 class="text-lg font-bold text-slate-800 mb-4 flex items-center space-x-2">
                        <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
                        <span>Student Report Card</span>
                    </h2>
                    <form action="${pageContext.request.contextPath}/staff/reports" method="get">
                        <input type="hidden" name="action" value="student">
                        <label for="studentId" class="block text-xs font-semibold text-slate-500 uppercase tracking-wider mb-2">Select Student Profile</label>
                        <select id="studentId" name="studentId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition mb-4">
                            <option value="">-- Choose Student --</option>
                            <%
                                List<Student> students = (List<Student>) request.getAttribute("students");
                                if (students != null) {
                                    for (Student student : students) {
                            %>
                                <option value="<%= student.getId() %>"><%= student.getName() %> (<%= student.getRollNo() %>)</option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <button type="submit" class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2.5 rounded-lg text-sm transition shadow-sm">
                            Generate Student Card
                        </button>
                    </form>
                </div>
            </div>

            <!-- Right Column: Sheet Exports & Prints -->
            <div class="lg:col-span-2 space-y-8">
                
                <!-- Attendance Sheets Section -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <div class="border-b border-slate-100 pb-4 mb-6">
                        <h2 class="text-xl font-bold text-slate-800 flex items-center space-x-2">
                            <svg class="w-6 h-6 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                            <span>Attendance Evaluation Reports</span>
                        </h2>
                        <p class="text-sm text-slate-500 mt-1">Generate complete class register sheets in Excel or printable PDF format.</p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <!-- Excel export option -->
                        <div class="border border-slate-200 rounded-xl p-5 hover:border-emerald-200 hover:bg-emerald-50/10 transition">
                            <div class="flex items-center space-x-3 mb-4">
                                <div class="bg-emerald-100 text-emerald-800 p-2.5 rounded-lg">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                                </div>
                                <div>
                                    <h3 class="font-bold text-slate-800 text-base">Attendance Excel</h3>
                                    <p class="text-xs text-slate-400">Download formatted XML-as-Excel sheets.</p>
                                </div>
                            </div>
                            <form action="${pageContext.request.contextPath}/staff/reports" method="get">
                                <input type="hidden" name="action" value="excel_attendance">
                                <select name="subjectId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-3 py-2 text-xs focus:ring-1 focus:ring-emerald-500 focus:border-emerald-500 mb-3.5">
                                    <option value="">-- Choose Subject --</option>
                                    <option value="1">Java Full Stack Dev (CSE-801)</option>
                                    <option value="2">Machine Learning (CSE-802)</option>
                                    <option value="3">Cloud Computing (CSE-803)</option>
                                </select>
                                <button type="submit" class="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-2 rounded text-xs transition">
                                    Export Excel File (.xls)
                                </button>
                            </form>
                        </div>

                        <!-- PDF/Print options -->
                        <div class="border border-slate-200 rounded-xl p-5 hover:border-indigo-200 hover:bg-indigo-50/10 transition">
                            <div class="flex items-center space-x-3 mb-4">
                                <div class="bg-indigo-100 text-indigo-800 p-2.5 rounded-lg">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"></path></svg>
                                </div>
                                <div>
                                    <h3 class="font-bold text-slate-800 text-base">Attendance PDF</h3>
                                    <p class="text-xs text-slate-400">Generate clean printable paper rosters.</p>
                                </div>
                            </div>
                            <form action="${pageContext.request.contextPath}/staff/reports" method="get" target="_blank">
                                <input type="hidden" name="action" value="print_attendance">
                                <select name="subjectId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-3 py-2 text-xs focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 mb-3.5">
                                    <option value="">-- Choose Subject --</option>
                                    <option value="1">Java Full Stack Dev (CSE-801)</option>
                                    <option value="2">Machine Learning (CSE-802)</option>
                                    <option value="3">Cloud Computing (CSE-803)</option>
                                </select>
                                <button type="submit" class="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 rounded text-xs transition">
                                    Generate Printable PDF
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Marks Sheets Section -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <div class="border-b border-slate-100 pb-4 mb-6">
                        <h2 class="text-xl font-bold text-slate-800 flex items-center space-x-2">
                            <svg class="w-6 h-6 text-teal-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 3.055A9.001 9.001 0 1020.945 13H11V3.055z"></path></svg>
                            <span>Internal Marks Grading Reports</span>
                        </h2>
                        <p class="text-sm text-slate-500 mt-1">Export continuous assessment sheets containing CIA1, CIA2, Model, Lab, Seminars.</p>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <!-- Excel export option -->
                        <div class="border border-slate-200 rounded-xl p-5 hover:border-emerald-200 hover:bg-emerald-50/10 transition">
                            <div class="flex items-center space-x-3 mb-4">
                                <div class="bg-emerald-100 text-emerald-800 p-2.5 rounded-lg">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                                </div>
                                <div>
                                    <h3 class="font-bold text-slate-800 text-base">Marks Excel</h3>
                                    <p class="text-xs text-slate-400">Download formatted XML-as-Excel sheets.</p>
                                </div>
                            </div>
                            <form action="${pageContext.request.contextPath}/staff/reports" method="get">
                                <input type="hidden" name="action" value="excel_marks">
                                <select name="subjectId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-3 py-2 text-xs focus:ring-1 focus:ring-emerald-500 focus:border-emerald-500 mb-3.5">
                                    <option value="">-- Choose Subject --</option>
                                    <option value="1">Java Full Stack Dev (CSE-801)</option>
                                    <option value="2">Machine Learning (CSE-802)</option>
                                    <option value="3">Cloud Computing (CSE-803)</option>
                                </select>
                                <button type="submit" class="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-semibold py-2 rounded text-xs transition">
                                    Export Excel File (.xls)
                                </button>
                            </form>
                        </div>

                        <!-- PDF/Print options -->
                        <div class="border border-slate-200 rounded-xl p-5 hover:border-teal-200 hover:bg-teal-50/10 transition">
                            <div class="flex items-center space-x-3 mb-4">
                                <div class="bg-teal-100 text-teal-800 p-2.5 rounded-lg">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"></path></svg>
                                </div>
                                <div>
                                    <h3 class="font-bold text-slate-800 text-base">Marks PDF</h3>
                                    <p class="text-xs text-slate-400">Generate clean printable student marks sheets.</p>
                                </div>
                            </div>
                            <form action="${pageContext.request.contextPath}/staff/reports" method="get" target="_blank">
                                <input type="hidden" name="action" value="print_marks">
                                <select name="subjectId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-3 py-2 text-xs focus:ring-1 focus:ring-teal-500 focus:border-teal-500 mb-3.5">
                                    <option value="">-- Choose Subject --</option>
                                    <option value="1">Java Full Stack Dev (CSE-801)</option>
                                    <option value="2">Machine Learning (CSE-802)</option>
                                    <option value="3">Cloud Computing (CSE-803)</option>
                                </select>
                                <button type="submit" class="w-full bg-teal-600 hover:bg-teal-700 text-white font-semibold py-2 rounded text-xs transition">
                                    Generate Printable PDF
                                </button>
                            </form>
                        </div>
                    </div>
                </div>

            </div>
        </div>

    </main>

    <!-- Footer Banner -->
    <footer class="bg-slate-900 text-slate-400 py-6 border-t border-slate-800">
        <div class="max-w-7xl mx-auto px-4 text-center text-xs">
            <p>&copy; 2026 SmartAttend College ERP portal. Crafted with real-time automatic reporting tools.</p>
        </div>
    </footer>

    <!-- ChartJS and script initializations -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            
            // 1. Line Chart: Weekly Attendance Trends
            const weeklyTrendsCtx = document.getElementById('weeklyTrendsChart').getContext('2d');
            new Chart(weeklyTrendsCtx, {
                type: 'line',
                data: {
                    labels: [
                        <%
                            List<Map<String, Object>> weeklyTrends = (List<Map<String, Object>>) (dashboardStats != null ? dashboardStats.get("weeklyTrends") : null);
                            if (weeklyTrends != null) {
                                for (int i = 0; i < weeklyTrends.size(); i++) {
                                    out.print("'" + weeklyTrends.get(i).get("date") + "'" + (i < weeklyTrends.size() - 1 ? "," : ""));
                                }
                            }
                        %>
                    ],
                    datasets: [{
                        label: 'Average Presence %',
                        data: [
                            <%
                                if (weeklyTrends != null) {
                                    for (int i = 0; i < weeklyTrends.size(); i++) {
                                        out.print(weeklyTrends.get(i).get("average") + (i < weeklyTrends.size() - 1 ? "," : ""));
                                    }
                                }
                            %>
                        ],
                        borderColor: '#4f46e5',
                        backgroundColor: 'rgba(79, 70, 229, 0.1)',
                        borderWidth: 2,
                        tension: 0.35,
                        fill: true,
                        pointBackgroundColor: '#4f46e5',
                        pointBorderColor: '#ffffff',
                        pointHoverRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            min: 0,
                            max: 100,
                            ticks: { callback: value => value + "%" },
                            grid: { color: 'rgba(241, 245, 249, 1)' }
                        },
                        x: {
                            grid: { display: false }
                        }
                    }
                }
            });

            // 2. Bar Chart: Subject comparison
            const subjectCtx = document.getElementById('subjectComparisonChart').getContext('2d');
            new Chart(subjectCtx, {
                type: 'bar',
                data: {
                    labels: [
                        <%
                            List<Map<String, Object>> subjectStats = (List<Map<String, Object>>) (dashboardStats != null ? dashboardStats.get("subjectStats") : null);
                            if (subjectStats != null) {
                                for (int i = 0; i < subjectStats.size(); i++) {
                                    out.print("'" + subjectStats.get(i).get("code") + "'" + (i < subjectStats.size() - 1 ? "," : ""));
                                }
                            }
                        %>
                    ],
                    datasets: [{
                        label: 'Avg Attendance %',
                        data: [
                            <%
                                if (subjectStats != null) {
                                    for (int i = 0; i < subjectStats.size(); i++) {
                                        out.print(subjectStats.get(i).get("avgAttendance") + (i < subjectStats.size() - 1 ? "," : ""));
                                    }
                                }
                            %>
                        ],
                        backgroundColor: '#0d9488',
                        hoverBackgroundColor: '#0f766e',
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            min: 0,
                            max: 100,
                            ticks: { callback: value => value + "%" },
                            grid: { color: 'rgba(241, 245, 249, 1)' }
                        },
                        x: {
                            grid: { display: false }
                        }
                    }
                }
            });

            // 3. Pie Chart: Standing
            const standingCtx = document.getElementById('standingChart').getContext('2d');
            new Chart(standingCtx, {
                type: 'pie',
                data: {
                    labels: ['Good Standing (>=75%)', 'At-Risk (<75%)'],
                    datasets: [{
                        data: [<%= goodStandingCount %>, <%= atRiskCount %>],
                        backgroundColor: ['#10b981', '#ef4444'],
                        hoverOffset: 4,
                        borderWidth: 1,
                        borderColor: '#ffffff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { position: 'bottom' }
                    }
                }
            });

        });
    </script>

</body>
</html>
