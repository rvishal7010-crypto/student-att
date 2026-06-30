<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.example.erp.model.Student"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartAttend ERP - <%= request.getAttribute("reportType") %></title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col">

    <!-- Header Navigation -->
    <header class="bg-slate-900 text-white shadow">
        <div class="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
            <div class="flex items-center space-x-3">
                <span class="text-2xl font-bold tracking-tight bg-gradient-to-r from-cyan-400 to-indigo-200 bg-clip-text text-transparent">SmartAttend ERP</span>
                <span class="bg-slate-800 text-xs text-slate-300 font-semibold px-2.5 py-1 rounded-full border border-slate-700">Analytic Viewer</span>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/staff/reports" class="bg-slate-800 hover:bg-slate-700 text-xs font-semibold px-4 py-2 rounded-lg transition border border-slate-700">
                    &larr; Back to Dashboard
                </a>
            </div>
        </div>
    </header>

    <!-- Main Workspace -->
    <main class="flex-grow max-w-7xl w-full mx-auto px-4 py-8">
        
        <!-- Report Header Panel -->
        <div class="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 border-b border-slate-200 pb-6">
            <div>
                <span class="text-xs font-bold text-indigo-600 uppercase tracking-widest">Academic Report</span>
                <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight mt-1"><%= request.getAttribute("reportType") %></h1>
                <p class="text-slate-500 text-sm mt-1">Generated dynamically on <%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()) %> UTC</p>
            </div>
            
            <!-- Quick Floating Action Links -->
            <div class="mt-4 md:mt-0 flex space-x-3">
                <% 
                    // Dynamic print link based on report types
                    String printUrl = "";
                    if (request.getAttribute("student") != null) {
                        Student s = (Student) request.getAttribute("student");
                        printUrl = request.getContextPath() + "/staff/reports?action=print_student&studentId=" + s.getId();
                    }
                %>
                <% if (!printUrl.isEmpty()) { %>
                    <a href="<%= printUrl %>" target="_blank" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-5 py-2.5 rounded-lg text-sm transition shadow flex items-center space-x-2">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"></path></svg>
                        <span>Print PDF / Save File</span>
                    </a>
                <% } else { %>
                    <button onclick="window.print()" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-5 py-2.5 rounded-lg text-sm transition shadow flex items-center space-x-2">
                        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"></path></svg>
                        <span>Print Report Page</span>
                    </button>
                <% } %>
            </div>
        </div>

        <!-- RENDER CHUNK: 1. OVERALL COLLEGE STATS -->
        <% if (request.getAttribute("overallStats") != null) { 
            Map<String, Object> stats = (Map<String, Object>) request.getAttribute("overallStats");
        %>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <!-- Students Count -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
                    <div>
                        <span class="text-sm font-semibold text-slate-500 uppercase tracking-wider">Total Registered Students</span>
                        <div class="text-4xl font-extrabold text-slate-900 mt-1"><%= stats.get("totalStudents") %></div>
                    </div>
                    <div class="bg-indigo-100 text-indigo-800 p-4 rounded-xl">
                        <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                    </div>
                </div>

                <!-- Departments Count -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
                    <div>
                        <span class="text-sm font-semibold text-slate-500 uppercase tracking-wider">Total Faculty / Staff</span>
                        <div class="text-4xl font-extrabold text-slate-900 mt-1"><%= stats.get("totalStaff") %></div>
                    </div>
                    <div class="bg-emerald-100 text-emerald-800 p-4 rounded-xl">
                        <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
                    </div>
                </div>

                <!-- Overall Attendance Percent -->
                <div class="bg-white p-6 rounded-xl border border-slate-200 shadow-sm flex items-center justify-between">
                    <div>
                        <span class="text-sm font-semibold text-slate-500 uppercase tracking-wider">Overall Attendance %</span>
                        <div class="text-4xl font-extrabold text-indigo-600 mt-1"><%= stats.get("overallAttendance") %>%</div>
                    </div>
                    <div class="bg-amber-100 text-amber-800 p-4 rounded-xl">
                        <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 002 2h2a2 2 0 002-2"></path></svg>
                    </div>
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <!-- Academic Evaluation Metrics -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <h3 class="text-lg font-bold text-slate-800 mb-4">Academic Pass / Score Distribution</h3>
                    <div class="space-y-4">
                        <div>
                            <div class="flex justify-between text-sm font-semibold mb-1">
                                <span class="text-slate-600">College-Wide Marks Performance</span>
                                <span class="text-indigo-600"><%= stats.get("overallMarks") %>%</span>
                            </div>
                            <div class="w-full bg-slate-100 rounded-full h-2.5">
                                <div class="bg-indigo-600 h-2.5 rounded-full" style="width: <%= stats.get("overallMarks") %>%"></div>
                            </div>
                        </div>
                        <div>
                            <div class="flex justify-between text-sm font-semibold mb-1">
                                <span class="text-slate-600">Attendance Benchmark Status (Min 75%)</span>
                                <span class="text-emerald-600"><%= stats.get("overallAttendance") %>%</span>
                            </div>
                            <div class="w-full bg-slate-100 rounded-full h-2.5">
                                <div class="bg-emerald-600 h-2.5 rounded-full" style="width: <%= stats.get("overallAttendance") %>%"></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Academic Configuration Details -->
                <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6">
                    <h3 class="text-lg font-bold text-slate-800 mb-4">Master Course Configurations</h3>
                    <div class="grid grid-cols-2 gap-4">
                        <div class="bg-slate-50 p-4 rounded-lg">
                            <span class="text-xs font-semibold text-slate-400 uppercase">Departments</span>
                            <div class="text-2xl font-extrabold text-slate-700 mt-1"><%= stats.get("totalDepartments") %></div>
                        </div>
                        <div class="bg-slate-50 p-4 rounded-lg">
                            <span class="text-xs font-semibold text-slate-400 uppercase">Academic Courses</span>
                            <div class="text-2xl font-extrabold text-slate-700 mt-1"><%= stats.get("totalCourses") %></div>
                        </div>
                    </div>
                </div>
            </div>
        <% } %>

        <!-- RENDER CHUNK: 2. DEPARTMENT REPORT -->
        <% if (request.getAttribute("deptStats") != null) { 
            List<Map<String, Object>> depts = (List<Map<String, Object>>) request.getAttribute("deptStats");
        %>
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                <div class="px-6 py-4 bg-slate-50 border-b border-slate-200">
                    <h3 class="text-lg font-bold text-slate-800">Department Metrics Summary</h3>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left border-collapse text-sm">
                        <thead>
                            <tr class="bg-slate-100 text-slate-600 uppercase text-xs font-bold tracking-wider border-b border-slate-200">
                                <th class="px-6 py-3">Code</th>
                                <th class="px-6 py-3">Department Name</th>
                                <th class="px-6 py-3 text-center">Students</th>
                                <th class="px-6 py-3 text-center">Courses</th>
                                <th class="px-6 py-3 text-center">Avg Attendance</th>
                                <th class="px-6 py-3 text-center">Avg Marks %</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-200">
                            <% for (Map<String, Object> dept : depts) { %>
                                <tr class="hover:bg-slate-50 transition">
                                    <td class="px-6 py-4 font-mono font-bold text-slate-600"><%= dept.get("deptCode") %></td>
                                    <td class="px-6 py-4 font-semibold text-slate-900"><%= dept.get("deptName") %></td>
                                    <td class="px-6 py-4 text-center text-slate-500 font-mono"><%= dept.get("studentCount") %></td>
                                    <td class="px-6 py-4 text-center text-slate-500 font-mono"><%= dept.get("courseCount") %></td>
                                    <td class="px-6 py-4 text-center">
                                        <span class="px-3 py-1 rounded-full text-xs font-bold font-mono bg-indigo-50 text-indigo-700 border border-indigo-100">
                                            <%= dept.get("avgAttendance") %>%
                                        </span>
                                    </td>
                                    <td class="px-6 py-4 text-center">
                                        <span class="px-3 py-1 rounded-full text-xs font-bold font-mono bg-emerald-50 text-emerald-700 border border-emerald-100">
                                            <%= dept.get("avgMarks") %>%
                                        </span>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        <% } %>

        <!-- RENDER CHUNK: 3. SEMESTER REPORT -->
        <% if (request.getAttribute("semStats") != null) { 
            List<Map<String, Object>> sems = (List<Map<String, Object>>) request.getAttribute("semStats");
        %>
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                <div class="px-6 py-4 bg-slate-50 border-b border-slate-200">
                    <h3 class="text-lg font-bold text-slate-800">Semester Metrics Distribution</h3>
                </div>
                <div class="overflow-x-auto">
                    <table class="w-full text-left border-collapse text-sm">
                        <thead>
                            <tr class="bg-slate-100 text-slate-600 uppercase text-xs font-bold tracking-wider border-b border-slate-200">
                                <th class="px-6 py-3">Academic Semester</th>
                                <th class="px-6 py-3">Academic Term / Year</th>
                                <th class="px-6 py-3 text-center">Students</th>
                                <th class="px-6 py-3 text-center">Avg Attendance</th>
                                <th class="px-6 py-3 text-center">Avg Academic Marks</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-200">
                            <% for (Map<String, Object> sem : sems) { %>
                                <tr class="hover:bg-slate-50 transition">
                                    <td class="px-6 py-4 font-bold text-slate-900">Semester <%= sem.get("semNumber") %></td>
                                    <td class="px-6 py-4 font-mono text-slate-500"><%= sem.get("academicYear") %></td>
                                    <td class="px-6 py-4 text-center text-slate-500 font-mono"><%= sem.get("studentCount") %></td>
                                    <td class="px-6 py-4 text-center">
                                        <span class="px-3 py-1 rounded-full text-xs font-bold font-mono bg-indigo-50 text-indigo-700 border border-indigo-100">
                                            <%= sem.get("avgAttendance") %>%
                                        </span>
                                    </td>
                                    <td class="px-6 py-4 text-center">
                                        <span class="px-3 py-1 rounded-full text-xs font-bold font-mono bg-teal-50 text-teal-700 border border-teal-100">
                                            <%= sem.get("avgMarks") %>%
                                        </span>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        <% } %>

        <!-- RENDER CHUNK: 4. INDIVIDUAL STUDENT REPORT CARD -->
        <% if (request.getAttribute("student") != null) { 
            Student student = (Student) request.getAttribute("student");
            List<Map<String, Object>> perf = (List<Map<String, Object>>) request.getAttribute("performance");
        %>
            <!-- Student Header Profile Card -->
            <div class="bg-slate-900 text-white p-6 rounded-t-xl flex flex-col md:flex-row justify-between items-start md:items-center space-y-4 md:space-y-0">
                <div>
                    <span class="bg-indigo-700 text-xs font-semibold px-2.5 py-1 rounded-full uppercase tracking-wider text-indigo-200">Academic Roll</span>
                    <h2 class="text-2xl font-extrabold tracking-tight mt-1"><%= student.getName() %></h2>
                    <p class="font-mono text-indigo-300 text-sm mt-0.5"><%= student.getRollNo() %></p>
                </div>
                <div class="grid grid-cols-2 md:grid-cols-3 gap-6 text-xs text-slate-400">
                    <div>
                        <span class="block uppercase font-bold text-slate-500">Contact Email</span>
                        <span class="text-white font-medium"><%= student.getEmail() %></span>
                    </div>
                    <div>
                        <span class="block uppercase font-bold text-slate-500">Phone Contact</span>
                        <span class="text-white font-medium"><%= student.getPhone() != null ? student.getPhone() : "N/A" %></span>
                    </div>
                </div>
            </div>

            <!-- Subject Academic Performance Sheets -->
            <div class="bg-white rounded-b-xl shadow-sm border-x border-b border-slate-200 overflow-hidden">
                <div class="px-6 py-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
                    <h3 class="text-md font-bold text-slate-800">Continuous Assessment Evaluations Summary</h3>
                    <div class="text-xs text-slate-400 font-medium">Evaluation sheet details all internal marks criteria</div>
                </div>
                
                <div class="overflow-x-auto">
                    <table class="w-full text-left border-collapse text-xs">
                        <thead>
                            <tr class="bg-slate-100 text-slate-600 uppercase font-bold tracking-wider text-xs border-b border-slate-200">
                                <th class="px-4 py-3">Subject</th>
                                <th class="px-3 py-3 text-center">Attendance<br><span class="text-slate-400 font-normal lowercase">(Classes)</span></th>
                                <th class="px-2 py-3 text-center">CIA1<br><span class="text-slate-400 font-normal lowercase">(50)</span></th>
                                <th class="px-2 py-3 text-center">CIA2<br><span class="text-slate-400 font-normal lowercase">(50)</span></th>
                                <th class="px-2 py-3 text-center">Model<br><span class="text-slate-400 font-normal lowercase">(100)</span></th>
                                <th class="px-2 py-3 text-center">Assign<br><span class="text-slate-400 font-normal lowercase">(10)</span></th>
                                <th class="px-2 py-3 text-center">Lab<br><span class="text-slate-400 font-normal lowercase">(50)</span></th>
                                <th class="px-2 py-3 text-center">Sem<br><span class="text-slate-400 font-normal lowercase">(20)</span></th>
                                <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-950">Total<br><span class="text-indigo-400 font-semibold lowercase">(280)</span></th>
                                <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-950">Average</th>
                                <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-950">Percentage</th>
                            </tr>
                        </thead>
                        <tbody class="divide-y divide-slate-200">
                            <% if (perf != null && !perf.isEmpty()) {
                                for (Map<String, Object> row : perf) {
                            %>
                                <tr class="hover:bg-slate-50 transition">
                                    <td class="px-4 py-4">
                                        <div class="font-bold text-slate-900 text-sm"><%= row.get("subjectName") %></div>
                                        <div class="font-mono text-slate-400 text-xs mt-0.5"><%= row.get("subjectCode") %></div>
                                    </td>
                                    <td class="px-3 py-4 text-center font-mono">
                                        <span class="block font-bold text-slate-800"><%= row.get("attendancePercentage") %>%</span>
                                        <span class="text-[10px] text-slate-400"><%= row.get("presentClasses") %> / <%= row.get("totalClasses") %> days</span>
                                    </td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("cia1") %></td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("cia2") %></td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("modelExam") %></td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("assignment") %></td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("lab") %></td>
                                    <td class="px-2 py-4 text-center font-semibold text-slate-700 font-mono"><%= row.get("seminar") %></td>
                                    
                                    <td class="px-4 py-4 text-center font-bold bg-indigo-50/30 text-indigo-950 font-mono text-sm"><%= row.get("marksTotal") %></td>
                                    <td class="px-4 py-4 text-center font-semibold bg-indigo-50/30 text-indigo-950 font-mono text-sm"><%= row.get("marksAverage") %></td>
                                    <td class="px-4 py-4 text-center font-extrabold bg-indigo-50/30 text-teal-600 font-mono text-sm"><%= row.get("marksPercentage") %>%</td>
                                </tr>
                            <% 
                                    }
                                } else {
                            %>
                                <tr>
                                    <td colspan="11" class="text-center py-8 text-slate-400">No subject marks registered under student's course context.</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        <% } %>

    </main>

    <!-- Footer Area -->
    <footer class="bg-slate-900 text-slate-400 py-6 border-t border-slate-800">
        <div class="max-w-7xl mx-auto px-4 text-center text-xs">
            <p>&copy; 2026 SmartAttend College ERP portal. Crafted with professional high-fidelity printable layouts.</p>
        </div>
    </footer>

</body>
</html>
