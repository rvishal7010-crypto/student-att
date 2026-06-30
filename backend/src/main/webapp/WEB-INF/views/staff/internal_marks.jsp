<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.example.erp.model.Student"%>
<%@ page import="com.example.erp.model.InternalMarks"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartAttend ERP - Internal Marks Module</title>
    <!-- Tailwind CSS CDN for high-end Visual Identity -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen flex flex-col">

    <!-- Top Navigation Header -->
    <header class="bg-indigo-900 text-white shadow-md">
        <div class="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
            <div class="flex items-center space-x-3">
                <span class="text-2xl font-bold tracking-tight bg-gradient-to-r from-teal-400 to-indigo-200 bg-clip-text text-transparent">SmartAttend ERP</span>
                <span class="bg-indigo-700 text-xs text-indigo-200 font-semibold px-2 py-1 rounded">Faculty Portal</span>
            </div>
            <div class="flex items-center space-x-4">
                <a href="${pageContext.request.contextPath}/staff/reports" class="text-sm font-medium text-teal-200 hover:text-white transition">Reports Dashboard</a>
                <span class="text-sm font-medium">Welcome, Instructor</span>
                <a href="${pageContext.request.contextPath}/logout" class="bg-indigo-700 hover:bg-indigo-800 text-xs font-semibold px-3 py-2 rounded-lg transition">Sign Out</a>
            </div>
        </div>
    </header>

    <!-- Main Workspace Container -->
    <main class="flex-grow max-w-7xl w-full mx-auto px-4 py-8">
        
        <!-- Breadcrumbs & Heading -->
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-slate-900 tracking-tight">Internal Marks Management</h1>
            <p class="text-slate-500 mt-1">Record continuous internal assessments (CIA1, CIA2, Model Exam, Assignment, Lab, Seminar)</p>
        </div>

        <!-- Success or Error Alerts -->
        <%
            String msg = request.getParameter("msg");
            String error = (String) request.getAttribute("error");
            if (msg == null) msg = request.getParameter("error"); // Fallback check
            if (msg != null && msg.equals("MarksSavedSuccessfully")) {
        %>
            <div class="bg-emerald-50 border-l-4 border-emerald-500 text-emerald-800 p-4 rounded-r-lg shadow-sm mb-6 flex items-center space-x-3">
                <svg class="w-5 h-5 text-emerald-500" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path></svg>
                <span class="font-medium">Success! Academic internal evaluation sheets successfully updated.</span>
            </div>
        <% } %>
        <% if (error != null) { %>
            <div class="bg-rose-50 border-l-4 border-rose-500 text-rose-800 p-4 rounded-r-lg shadow-sm mb-6 flex items-center space-x-3">
                <svg class="w-5 h-5 text-rose-500" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"></path></svg>
                <span><%= error %></span>
            </div>
        <% } %>

        <!-- Configuration Panel Card -->
        <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-6 mb-8">
            <h2 class="text-lg font-semibold text-slate-800 mb-4">Select Subject & Retrieve Evaluation Sheet</h2>
            <form action="${pageContext.request.contextPath}/staff/marks" method="get" class="grid grid-cols-1 md:grid-cols-3 gap-6 items-end">
                <input type="hidden" name="action" value="retrieve">
                
                <div>
                    <label for="subjectId" class="block text-sm font-semibold text-slate-700 mb-2">Academic Subject</label>
                    <select id="subjectId" name="subjectId" required class="w-full bg-slate-50 border border-slate-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition">
                        <option value="">-- Choose Subject --</option>
                        <option value="1" <%= "1".equals(request.getParameter("subjectId")) ? "selected" : "" %>>Java Full Stack Dev (CSE-801)</option>
                        <option value="2" <%= "2".equals(request.getParameter("subjectId")) ? "selected" : "" %>>Machine Learning (CSE-802)</option>
                        <option value="3" <%= "3".equals(request.getParameter("subjectId")) ? "selected" : "" %>>Cloud Computing (CSE-803)</option>
                    </select>
                </div>

                <div class="md:col-span-2">
                    <button type="submit" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-6 py-3 rounded-lg transition duration-200 shadow-sm">
                        Load Student Evaluation Sheet
                    </button>
                </div>
            </form>
        </div>

        <!-- Student Marks Entry Form Panel -->
        <% if (request.getAttribute("marksLoaded") != null && Boolean.TRUE.equals(request.getAttribute("marksLoaded"))) { %>
            <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
                <div class="px-6 py-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center">
                    <div>
                        <h3 class="text-lg font-semibold text-slate-800">Student Internal Grading Matrix</h3>
                        <p class="text-sm text-slate-500">Maximum possible weightage marks: CIA1: 50, CIA2: 50, Model Exam: 100, Assignment: 10, Lab: 50, Seminar: 20</p>
                    </div>
                    <div class="bg-indigo-100 text-indigo-800 px-3 py-1 rounded text-xs font-semibold">
                        Max: 280 Marks
                    </div>
                </div>

                <form action="${pageContext.request.contextPath}/staff/marks" method="post" class="p-6">
                    <input type="hidden" name="subjectId" value="<%= request.getParameter("subjectId") %>">
                    
                    <div class="overflow-x-auto">
                        <table class="w-full border-collapse text-left text-sm">
                            <thead>
                                <tr class="bg-slate-100 text-slate-600 uppercase tracking-wider text-xs font-bold border-b border-slate-200">
                                    <th class="px-4 py-3">Student Name</th>
                                    <th class="px-4 py-3">Roll No</th>
                                    <th class="px-3 py-3 text-center">CIA1<br><span class="text-slate-400 font-normal lowercase">(Max 50)</span></th>
                                    <th class="px-3 py-3 text-center">CIA2<br><span class="text-slate-400 font-normal lowercase">(Max 50)</span></th>
                                    <th class="px-3 py-3 text-center">Model Exam<br><span class="text-slate-400 font-normal lowercase">(Max 100)</span></th>
                                    <th class="px-3 py-3 text-center">Assignment<br><span class="text-slate-400 font-normal lowercase">(Max 10)</span></th>
                                    <th class="px-3 py-3 text-center">Lab<br><span class="text-slate-400 font-normal lowercase">(Max 50)</span></th>
                                    <th class="px-3 py-3 text-center">Seminar<br><span class="text-slate-400 font-normal lowercase">(Max 20)</span></th>
                                    <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-900">Total<br><span class="text-slate-400 font-normal lowercase">(280)</span></th>
                                    <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-900">Average</th>
                                    <th class="px-4 py-3 text-center bg-indigo-50/50 text-indigo-900">Percentage</th>
                                </tr>
                            </thead>
                            <tbody class="divide-y divide-slate-200">
                                <%
                                    List<Student> students = (List<Student>) request.getAttribute("studentList");
                                    Map<Integer, InternalMarks> marksMap = (Map<Integer, InternalMarks>) request.getAttribute("marksMap");
                                    if (students != null && !students.isEmpty()) {
                                        for (Student student : students) {
                                            InternalMarks m = marksMap.get(student.getId());
                                %>
                                <tr class="hover:bg-slate-50 transition duration-150">
                                    <td class="px-4 py-3.5 font-medium text-slate-900">
                                        <%= student.getName() %>
                                        <input type="hidden" name="studentIds" value="<%= student.getId() %>">
                                    </td>
                                    <td class="px-4 py-3.5 text-slate-500 font-mono text-xs">
                                        <%= student.getRollNo() %>
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="50" id="cia1_<%= student.getId() %>" name="cia1_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getCia1() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="50" id="cia2_<%= student.getId() %>" name="cia2_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getCia2() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="100" id="modelExam_<%= student.getId() %>" name="modelExam_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getModelExam() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="10" id="assignment_<%= student.getId() %>" name="assignment_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getAssignment() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="50" id="lab_<%= student.getId() %>" name="lab_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getLab() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-3 py-3.5">
                                        <input type="number" step="0.01" min="0" max="20" id="seminar_<%= student.getId() %>" name="seminar_<%= student.getId() %>" 
                                               value="<%= m != null ? m.getSeminar() : "0.00" %>" oninput="calculateRow(<%= student.getId() %>)"
                                               class="w-20 bg-white border border-slate-300 rounded text-center py-1.5 focus:ring-1 focus:ring-indigo-500 focus:border-indigo-500 transition">
                                    </td>
                                    <td class="px-4 py-3.5 text-center font-bold bg-indigo-50/30 text-indigo-900 font-mono text-sm" id="total_<%= student.getId() %>">
                                        <%= m != null ? m.getTotal() : "0.00" %>
                                    </td>
                                    <td class="px-4 py-3.5 text-center font-semibold bg-indigo-50/30 text-indigo-900 font-mono text-sm" id="average_<%= student.getId() %>">
                                        <%= m != null ? m.getAverage() : "0.00" %>
                                    </td>
                                    <td class="px-4 py-3.5 text-center font-bold bg-indigo-50/30 text-indigo-900 font-mono text-sm" id="percentage_<%= student.getId() %>">
                                        <%= m != null ? m.getPercentage() : "0.00" %>%
                                    </td>
                                </tr>
                                <% 
                                        }
                                    } else {
                                %>
                                <tr>
                                    <td colspan="11" class="text-center py-8 text-slate-400">No students registered in this subject's course.</td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>

                    <div class="mt-8 flex justify-end space-x-4 border-t border-slate-100 pt-6">
                        <button type="button" onclick="calculateAll()" class="bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold px-5 py-2.5 rounded-lg transition duration-200">
                            Force Calculate All
                        </button>
                        <button type="submit" class="bg-teal-600 hover:bg-teal-700 text-white font-semibold px-6 py-2.5 rounded-lg transition duration-200 shadow-sm">
                            Commit & Save Evaluation sheet
                        </button>
                    </div>
                </form>
            </div>
        <% } %>
    </main>

    <!-- Footer Area -->
    <footer class="bg-slate-900 text-slate-400 py-6 border-t border-slate-800">
        <div class="max-w-7xl mx-auto px-4 text-center text-xs">
            <p>&copy; 2026 SmartAttend College ERP portal. Crafted with real-time automatic grading matrix metrics.</p>
        </div>
    </footer>

    <!-- Client-Side Calculation engine representing real-time UI interactivity -->
    <script>
        function calculateRow(studentId) {
            // Get values
            const cia1 = parseFloat(document.getElementById('cia1_' + studentId).value) || 0;
            const cia2 = parseFloat(document.getElementById('cia2_' + studentId).value) || 0;
            const modelExam = parseFloat(document.getElementById('modelExam_' + studentId).value) || 0;
            const assignment = parseFloat(document.getElementById('assignment_' + studentId).value) || 0;
            const lab = parseFloat(document.getElementById('lab_' + studentId).value) || 0;
            const seminar = parseFloat(document.getElementById('seminar_' + studentId).value) || 0;

            // Maximum possible constraints validation
            if (cia1 > 50) alert("CIA1 marks cannot exceed 50.");
            if (cia2 > 50) alert("CIA2 marks cannot exceed 50.");
            if (modelExam > 100) alert("Model Exam marks cannot exceed 100.");
            if (assignment > 10) alert("Assignment marks cannot exceed 10.");
            if (lab > 50) alert("Lab marks cannot exceed 50.");
            if (seminar > 20) alert("Seminar marks cannot exceed 20.");

            // Calculate total
            const total = cia1 + cia2 + modelExam + assignment + lab + seminar;
            
            // Calculate average (6 components)
            const average = total / 6.0;

            // Calculate percentage based on max 280
            const percentage = (total / 280.0) * 100.0;

            // Update UI elements
            document.getElementById('total_' + studentId).innerText = total.toFixed(2);
            document.getElementById('average_' + studentId).innerText = average.toFixed(2);
            document.getElementById('percentage_' + studentId).innerText = percentage.toFixed(2) + '%';
        }

        function calculateAll() {
            const studentInputs = document.getElementsByName('studentIds');
            studentInputs.forEach(input => {
                calculateRow(input.value);
            });
        }
    </script>
</body>
</html>
