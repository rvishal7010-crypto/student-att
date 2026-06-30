package com.example.erp.servlet;

import com.example.erp.dao.AttendanceDAO;
import com.example.erp.dao.NotificationDAO;
import com.example.erp.dao.ReportDAO;
import com.example.erp.dao.StudentDAO;
import com.example.erp.model.Notification;
import com.example.erp.model.Student;
import com.example.erp.model.UserLogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Controller servlet handling formal reporting operations.
 * Generates Attendance PDF/Print templates, Attendance Excel sheets, Marks PDF/Print templates, 
 * Marks Excel sheets, Department, Semester, Student-card, and Overall College academic status logs.
 */
@WebServlet("/staff/reports")
public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ReportDAO reportDAO;
    private StudentDAO studentDAO;
    private AttendanceDAO attendanceDAO;

    @Override
    public void init() throws ServletException {
        this.reportDAO = new ReportDAO();
        this.studentDAO = new StudentDAO();
        this.attendanceDAO = new AttendanceDAO();
    }

    private boolean checkStaffAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null || 
            !"STAFF".equals(session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=UnauthorizedAccess");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkStaffAccess(request, response)) return;

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "dashboard";
        }

        switch (action) {
            case "dashboard":
                showDashboard(request, response);
                break;
            case "department":
                generateDepartmentReport(request, response);
                break;
            case "semester":
                generateSemesterReport(request, response);
                break;
            case "student":
                generateStudentReport(request, response);
                break;
            case "overall":
                generateOverallReport(request, response);
                break;
            case "excel_attendance":
                exportAttendanceExcel(request, response);
                break;
            case "excel_marks":
                exportMarksExcel(request, response);
                break;
            case "print_attendance":
                renderPrintAttendance(request, response);
                break;
            case "print_marks":
                renderPrintMarks(request, response);
                break;
            case "print_student":
                renderPrintStudent(request, response);
                break;
            default:
                showDashboard(request, response);
                break;
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Load Students and Subjects to populate dropdowns
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        
        // Fetch dashboard metrics
        Map<String, Object> stats = reportDAO.getDashboardStats();
        request.setAttribute("dashboardStats", stats);
        
        // Fetch circular notifications
        NotificationDAO notificationDAO = new NotificationDAO();
        List<Notification> notifications = notificationDAO.getNotificationsForRole("STAFF");
        request.setAttribute("notifications", notifications);
        
        // Fetch recent reports as recent activity
        List<com.example.erp.model.Report> recentReports = reportDAO.getAllReports();
        if (recentReports != null && recentReports.size() > 5) {
            recentReports = recentReports.subList(0, 5);
        }
        request.setAttribute("recentActivities", recentReports);
        
        request.getRequestDispatcher("/WEB-INF/views/staff/reports_dashboard.jsp").forward(request, response);
    }

    private void generateDepartmentReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Map<String, Object>> deptStats = reportDAO.getDepartmentStats();
        request.setAttribute("deptStats", deptStats);
        request.setAttribute("reportType", "Department Academic Distribution Overview");
        request.getRequestDispatcher("/WEB-INF/views/staff/report_view.jsp").forward(request, response);
    }

    private void generateSemesterReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Map<String, Object>> semStats = reportDAO.getSemesterStats();
        request.setAttribute("semStats", semStats);
        request.setAttribute("reportType", "Semester Performance Distribution Matrix");
        request.getRequestDispatcher("/WEB-INF/views/staff/report_view.jsp").forward(request, response);
    }

    private void generateStudentReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            Student student = studentDAO.getStudentById(studentId);
            if (student != null) {
                List<Map<String, Object>> performance = reportDAO.getStudentSubjectPerformance(
                        student.getId(), student.getCourseId(), student.getSemesterId());
                request.setAttribute("student", student);
                request.setAttribute("performance", performance);
                request.setAttribute("reportType", "Individual Student Academic Profile");
                request.getRequestDispatcher("/WEB-INF/views/staff/report_view.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/reports?error=StudentNotFound");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=InvalidStudentID");
        }
    }

    private void generateOverallReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        Map<String, Object> overallStats = reportDAO.getOverallStats();
        request.setAttribute("overallStats", overallStats);
        request.setAttribute("reportType", "Overall College Academic Progress Statement");
        request.getRequestDispatcher("/WEB-INF/views/staff/report_view.jsp").forward(request, response);
    }

    private String getSubjectName(int subjectId) {
        switch (subjectId) {
            case 1: return "Java Full Stack Dev (CSE-801)";
            case 2: return "Machine Learning (CSE-802)";
            case 3: return "Cloud Computing (CSE-803)";
            default: return "Unknown Academic Course";
        }
    }

    private void exportAttendanceExcel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            List<Map<String, Object>> data = reportDAO.getAttendanceReportData(subjectId);
            String subjectName = getSubjectName(subjectId);

            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"attendance_report_subject_" + subjectId + ".xls\"");

            PrintWriter out = response.getWriter();
            out.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
            out.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><!--[if gte o:office:office]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>Attendance</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head>");
            out.println("<body style='font-family: Arial, sans-serif;'>");
            out.println("<h2>Attendance Report - " + subjectName + "</h2>");
            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            out.println("<tr style='background-color: #312E81; color: #FFFFFF; font-weight: bold;'>");
            out.println("<th>Roll No</th>");
            out.println("<th>Student Name</th>");
            out.println("<th>Present Classes</th>");
            out.println("<th>Total Classes</th>");
            out.println("<th>Attendance Percentage</th>");
            out.println("</tr>");

            for (Map<String, Object> row : data) {
                out.println("<tr>");
                out.println("<td style='font-family: monospace;'>" + row.get("studentRoll") + "</td>");
                out.println("<td>" + row.get("studentName") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("presentClasses") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("totalClasses") + "</td>");
                out.println("<td style='text-align: right; font-weight: bold;'>" + row.get("attendancePercentage") + "%</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=ExportFailed");
        }
    }

    private void exportMarksExcel(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            List<Map<String, Object>> data = reportDAO.getMarksReportData(subjectId);
            String subjectName = getSubjectName(subjectId);

            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"internal_marks_report_subject_" + subjectId + ".xls\"");

            PrintWriter out = response.getWriter();
            out.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
            out.println("<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><!--[if gte o:office:office]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>Marks Sheet</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head>");
            out.println("<body style='font-family: Arial, sans-serif;'>");
            out.println("<h2>Internal Marks Report - " + subjectName + "</h2>");
            out.println("<table border='1' cellpadding='5' cellspacing='0'>");
            out.println("<tr style='background-color: #0F766E; color: #FFFFFF; font-weight: bold;'>");
            out.println("<th>Roll No</th>");
            out.println("<th>Student Name</th>");
            out.println("<th>CIA1 (50)</th>");
            out.println("<th>CIA2 (50)</th>");
            out.println("<th>Model Exam (100)</th>");
            out.println("<th>Assignment (10)</th>");
            out.println("<th>Lab (50)</th>");
            out.println("<th>Seminar (20)</th>");
            out.println("<th>Total (280)</th>");
            out.println("<th>Average</th>");
            out.println("<th>Percentage</th>");
            out.println("</tr>");

            for (Map<String, Object> row : data) {
                out.println("<tr>");
                out.println("<td style='font-family: monospace;'>" + row.get("studentRoll") + "</td>");
                out.println("<td>" + row.get("studentName") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("cia1") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("cia2") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("modelExam") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("assignment") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("lab") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("seminar") + "</td>");
                out.println("<td style='text-align: center; font-weight: bold;'>" + row.get("total") + "</td>");
                out.println("<td style='text-align: center;'>" + row.get("average") + "</td>");
                out.println("<td style='text-align: right; font-weight: bold; color: #0f766e;'>" + row.get("percentage") + "%</td>");
                out.println("</tr>");
            }
            out.println("</table>");
            out.println("</body></html>");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=ExportFailed");
        }
    }

    private void renderPrintAttendance(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            List<Map<String, Object>> data = reportDAO.getAttendanceReportData(subjectId);
            request.setAttribute("printData", data);
            request.setAttribute("printTitle", "Subject Attendance Assessment Roll");
            request.setAttribute("printSubtitle", getSubjectName(subjectId));
            request.setAttribute("printType", "attendance");
            request.getRequestDispatcher("/WEB-INF/views/staff/print_template.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=PrintFailed");
        }
    }

    private void renderPrintMarks(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            List<Map<String, Object>> data = reportDAO.getMarksReportData(subjectId);
            request.setAttribute("printData", data);
            request.setAttribute("printTitle", "Subject Internal Marks Grade Sheet");
            request.setAttribute("printSubtitle", getSubjectName(subjectId));
            request.setAttribute("printType", "marks");
            request.getRequestDispatcher("/WEB-INF/views/staff/print_template.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=PrintFailed");
        }
    }

    private void renderPrintStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            Student student = studentDAO.getStudentById(studentId);
            if (student != null) {
                List<Map<String, Object>> performance = reportDAO.getStudentSubjectPerformance(
                        student.getId(), student.getCourseId(), student.getSemesterId());
                request.setAttribute("student", student);
                request.setAttribute("performance", performance);
                request.setAttribute("printTitle", "Student Comprehensive Academic Record");
                request.setAttribute("printSubtitle", student.getName() + " (" + student.getRollNo() + ")");
                request.setAttribute("printType", "student");
                request.getRequestDispatcher("/WEB-INF/views/staff/print_template.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/reports?error=StudentNotFound");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/reports?error=PrintFailed");
        }
    }
}
