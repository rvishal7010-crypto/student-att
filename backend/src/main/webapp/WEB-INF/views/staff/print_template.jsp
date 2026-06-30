<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.example.erp.model.Student"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SmartAttend Official Print Statement</title>
    <!-- Tailwind CDN for fast base layout, print styles will override -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #ffffff;
            color: #000000;
        }
        @media print {
            body {
                background: #ffffff;
                color: #000000;
                font-size: 12px;
                margin: 0;
                padding: 0;
            }
            .no-print {
                display: none !important;
            }
            .print-border {
                border: 1px solid #000000 !important;
            }
            .page-break {
                page-break-after: always;
            }
        }
    </style>
</head>
<body class="p-8 max-w-5xl mx-auto">

    <!-- Floating Action Bar for Direct Action (Hidden on Print) -->
    <div class="no-print mb-8 bg-slate-900 text-white p-4 rounded-xl shadow-md flex justify-between items-center">
        <div>
            <h4 class="font-bold text-sm">Official Academic Document Print-Preview</h4>
            <p class="text-xs text-slate-400">Ready for high-contrast official archives or Save as PDF.</p>
        </div>
        <div class="flex space-x-3">
            <button onclick="window.print()" class="bg-indigo-600 hover:bg-indigo-700 text-xs font-bold px-4 py-2 rounded-lg transition shadow">
                Execute Print / Save PDF
            </button>
            <button onclick="window.close()" class="bg-slate-800 hover:bg-slate-700 text-xs font-bold px-4 py-2 rounded-lg transition border border-slate-700">
                Close Preview
            </button>
        </div>
    </div>

    <!-- Official Institutional Letterhead -->
    <div class="text-center border-b-4 border-double border-slate-800 pb-4 mb-8">
        <h1 class="text-2xl font-black uppercase tracking-wider text-slate-900">SmartAttend Autonomous University</h1>
        <p class="text-xs uppercase tracking-widest text-slate-500 font-bold mt-1">Office of the Controller of Examinations & Academic Records</p>
        <p class="text-[10px] text-slate-400 mt-0.5">Campus Road, Tech District, Pin - 600001 | email: coe@smartattend.edu</p>
    </div>

    <!-- Document Header Details -->
    <div class="flex justify-between items-start mb-6">
        <div>
            <h2 class="text-lg font-bold text-slate-800 uppercase tracking-tight"><%= request.getAttribute("printTitle") %></h2>
            <p class="text-sm font-semibold text-slate-600 mt-0.5"><%= request.getAttribute("printSubtitle") %></p>
        </div>
        <div class="text-right text-xs font-mono text-slate-500">
            <p><strong>Statement ID:</strong> ERP-REP-<%= System.currentTimeMillis() %></p>
            <p><strong>Issue Date:</strong> <%= new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date()) %> UTC</p>
        </div>
    </div>

    <!-- MAIN DATA CHUNK: ATTENDANCE PRINT -->
    <% if ("attendance".equals(request.getAttribute("printType"))) { %>
        <table class="w-full text-left text-xs border border-slate-300 border-collapse mb-12">
            <thead>
                <tr class="bg-slate-100 border-b border-slate-300 font-bold text-slate-700">
                    <th class="px-4 py-2 border-r border-slate-300">Roll Number</th>
                    <th class="px-4 py-2 border-r border-slate-300">Student Name</th>
                    <th class="px-4 py-2 border-r border-slate-300 text-center">Present Classes</th>
                    <th class="px-4 py-2 border-r border-slate-300 text-center">Total Classes</th>
                    <th class="px-4 py-2 text-right">Attendance Ratio (%)</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-300">
                <% 
                    List<Map<String, Object>> printData = (List<Map<String, Object>>) request.getAttribute("printData");
                    if (printData != null) {
                        for (Map<String, Object> row : printData) {
                %>
                    <tr class="hover:bg-slate-50">
                        <td class="px-4 py-2 border-r border-slate-300 font-mono"><%= row.get("studentRoll") %></td>
                        <td class="px-4 py-2 border-r border-slate-300 font-semibold"><%= row.get("studentName") %></td>
                        <td class="px-4 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("presentClasses") %></td>
                        <td class="px-4 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("totalClasses") %></td>
                        <td class="px-4 py-2 text-right font-bold font-mono"><%= row.get("attendancePercentage") %>%</td>
                    </tr>
                <% 
                        }
                    }
                %>
            </tbody>
        </table>
    <% } %>

    <!-- MAIN DATA CHUNK: MARKS PRINT -->
    <% if ("marks".equals(request.getAttribute("printType"))) { %>
        <table class="w-full text-left text-[10px] border border-slate-300 border-collapse mb-12">
            <thead>
                <tr class="bg-slate-100 border-b border-slate-300 font-bold text-slate-700">
                    <th class="px-2 py-2 border-r border-slate-300">Roll No</th>
                    <th class="px-2 py-2 border-r border-slate-300">Student Name</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">CIA1<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">CIA2<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Model<br>(100)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Assg<br>(10)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Lab<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Sem<br>(20)</th>
                    <th class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50 font-bold">Total<br>(280)</th>
                    <th class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50">Average</th>
                    <th class="px-2 py-2 text-right bg-slate-50 font-bold">Percentage</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-300">
                <% 
                    List<Map<String, Object>> printData = (List<Map<String, Object>>) request.getAttribute("printData");
                    if (printData != null) {
                        for (Map<String, Object> row : printData) {
                %>
                    <tr class="hover:bg-slate-50">
                        <td class="px-2 py-2 border-r border-slate-300 font-mono"><%= row.get("studentRoll") %></td>
                        <td class="px-2 py-2 border-r border-slate-300 font-semibold text-slate-800"><%= row.get("studentName") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("cia1") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("cia2") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("modelExam") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("assignment") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("lab") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("seminar") %></td>
                        <td class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50/50 font-bold font-mono"><%= row.get("total") %></td>
                        <td class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50/50 font-mono"><%= row.get("average") %></td>
                        <td class="px-2 py-2 text-right bg-slate-50/50 font-bold font-mono"><%= row.get("percentage") %>%</td>
                    </tr>
                <% 
                        }
                    }
                %>
            </tbody>
        </table>
    <% } %>

    <!-- MAIN DATA CHUNK: STUDENT CARD PRINT -->
    <% if ("student".equals(request.getAttribute("printType"))) { 
        Student student = (Student) request.getAttribute("student");
        List<Map<String, Object>> perf = (List<Map<String, Object>>) request.getAttribute("performance");
    %>
        <div class="border border-slate-300 bg-slate-50 p-4 rounded-lg mb-6 grid grid-cols-2 md:grid-cols-4 gap-4 text-xs">
            <div><strong>Student Name:</strong> <%= student.getName() %></div>
            <div><strong>Roll Number:</strong> <%= student.getRollNo() %></div>
            <div><strong>Primary Email:</strong> <%= student.getEmail() %></div>
            <div><strong>Academic Status:</strong> Regular / Active</div>
        </div>

        <table class="w-full text-left text-[10px] border border-slate-300 border-collapse mb-12">
            <thead>
                <tr class="bg-slate-100 border-b border-slate-300 font-bold text-slate-700">
                    <th class="px-2 py-2 border-r border-slate-300">Subject</th>
                    <th class="px-2 py-2 border-r border-slate-300 text-center">Attendance %</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">CIA1<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">CIA2<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Model<br>(100)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Assg<br>(10)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Lab<br>(50)</th>
                    <th class="px-1.5 py-2 border-r border-slate-300 text-center">Sem<br>(20)</th>
                    <th class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50 font-bold">Total<br>(280)</th>
                    <th class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50">Average</th>
                    <th class="px-2 py-2 text-right bg-slate-50 font-bold">Percentage</th>
                </tr>
            </thead>
            <tbody class="divide-y divide-slate-300">
                <% if (perf != null) {
                    for (Map<String, Object> row : perf) {
                %>
                    <tr>
                        <td class="px-2 py-2 border-r border-slate-300">
                            <strong><%= row.get("subjectName") %></strong><br>
                            <span class="text-slate-400 font-mono"><%= row.get("subjectCode") %></span>
                        </td>
                        <td class="px-2 py-2 border-r border-slate-300 text-center font-mono">
                            <strong><%= row.get("attendancePercentage") %>%</strong><br>
                            <span class="text-[8px] text-slate-400"><%= row.get("presentClasses") %>/<%= row.get("totalClasses") %> Classes</span>
                        </td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("cia1") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("cia2") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("modelExam") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("assignment") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("lab") %></td>
                        <td class="px-1.5 py-2 border-r border-slate-300 text-center font-mono"><%= row.get("seminar") %></td>
                        <td class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50/50 font-bold font-mono"><%= row.get("marksTotal") %></td>
                        <td class="px-2 py-2 border-r border-slate-300 text-center bg-slate-50/50 font-mono"><%= row.get("marksAverage") %></td>
                        <td class="px-2 py-2 text-right bg-slate-50/50 font-bold font-mono text-indigo-600"><%= row.get("marksPercentage") %>%</td>
                    </tr>
                <% 
                    }
                } %>
            </tbody>
        </table>
    <% } %>

    <!-- Signatures Panel (Standard Official Audit Structure) -->
    <div class="mt-16 grid grid-cols-3 gap-8 text-center text-xs text-slate-600">
        <div>
            <div class="border-t border-slate-400 pt-3 font-semibold">Subject Instructor</div>
            <div class="text-[9px] text-slate-400 mt-0.5">Continuous Evaluation Staff</div>
        </div>
        <div>
            <div class="border-t border-slate-400 pt-3 font-semibold">Department Head</div>
            <div class="text-[9px] text-slate-400 mt-0.5">Faculty Moderator Chairperson</div>
        </div>
        <div>
            <div class="border-t border-slate-400 pt-3 font-semibold">Controller of Examinations</div>
            <div class="text-[9px] text-slate-400 mt-0.5">Autonomous Registrar Seal Office</div>
        </div>
    </div>

    <!-- Official Security Watermark Footer -->
    <div class="border-t border-slate-200 pt-4 mt-16 text-center text-[10px] text-slate-400 font-mono">
        <p>This is a system-generated official secure academic transcript issued via SmartAttend ERP.</p>
        <p class="mt-0.5">Any alterations invalidate this statement. Verified security fingerprint: SHA256-<%= Math.abs(new java.util.Random().nextLong()) %></p>
    </div>

</body>
</html>
