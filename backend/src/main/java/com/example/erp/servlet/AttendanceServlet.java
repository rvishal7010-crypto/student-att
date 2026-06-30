package com.example.erp.servlet;

import com.example.erp.dao.AttendanceDAO;
import com.example.erp.dao.StaffDAO;
import com.example.erp.model.Attendance;
import com.example.erp.model.Staff;
import com.example.erp.model.Student;
import com.example.erp.model.UserLogin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller servlet handling Student Checklist Loading and batch Attendance marking.
 */
@WebServlet("/staff/attendance")
public class AttendanceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AttendanceDAO attendanceDAO;
    private StaffDAO staffDAO;

    @Override
    public void init() throws ServletException {
        this.attendanceDAO = new AttendanceDAO();
        this.staffDAO = new StaffDAO();
    }

    /**
     * Staff role authentication guard check.
     */
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

    /**
     * Renders empty attendance page, or retrieves students checklist.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkStaffAccess(request, response)) return;

        String action = request.getParameter("action");
        if ("retrieve".equals(action)) {
            retrieveChecklist(request, response);
        } else {
            // Render basic view
            request.getRequestDispatcher("/WEB-INF/views/staff/attendance_dashboard.jsp").forward(request, response);
        }
    }

    /**
     * Receives attendance batch records and persists them.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkStaffAccess(request, response)) return;

        // Fetch session data
        HttpSession session = request.getSession(false);
        UserLogin loggedInUser = (UserLogin) session.getAttribute("user");
        
        // Find linked Staff entity
        Staff staff = staffDAO.getStaffByLoginId(loggedInUser.getId());
        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard?error=StaffProfileMissing");
            return;
        }

        // Parse query parameters
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        String dateStr = request.getParameter("attendanceDate");
        Date attendanceDate = Date.valueOf(dateStr); // Converts yyyy-mm-dd string to SQL Date

        // List of all students that were in the checklist
        String[] studentIdStrings = request.getParameterValues("allStudentIds");
        
        if (studentIdStrings != null && studentIdStrings.length > 0) {
            List<Attendance> batchList = new ArrayList<>();
            
            // Read checkboxes
            // Checked checkboxes are sent as parameter values of "presentStudents"
            String[] presentStudentIdStrings = request.getParameterValues("presentStudents");
            List<Integer> presentIds = new ArrayList<>();
            if (presentStudentIdStrings != null) {
                for (String idStr : presentStudentIdStrings) {
                    presentIds.add(Integer.parseInt(idStr));
                }
            }

            for (String sIdStr : studentIdStrings) {
                int studentId = Integer.parseInt(sIdStr);
                String status = presentIds.contains(studentId) ? "PRESENT" : "ABSENT";
                
                Attendance record = new Attendance();
                record.setStudentId(studentId);
                record.setSubjectId(subjectId);
                record.setDate(attendanceDate);
                record.setStatus(status);
                record.setMarkedBy(staff.getId()); // ID of Staff marking it
                
                batchList.add(record);
            }

            boolean success = attendanceDAO.saveAttendanceBatch(batchList);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/staff/attendance?msg=AttendanceMarkedSuccessfully");
            } else {
                request.setAttribute("error", "Database Error: Failed to commit attendance batch record.");
                request.getRequestDispatcher("/WEB-INF/views/staff/attendance_dashboard.jsp").forward(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/staff/attendance?msg=NoStudentsToMark");
        }
    }

    private void retrieveChecklist(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            String dateStr = request.getParameter("attendanceDate");
            
            List<Student> studentList = attendanceDAO.getStudentsForMarking(subjectId);
            
            request.setAttribute("studentList", studentList);
            request.setAttribute("subjectId", subjectId);
            request.setAttribute("attendanceDate", dateStr);
            request.setAttribute("checklistLoaded", true);
            
            request.getRequestDispatcher("/WEB-INF/views/staff/attendance_dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            request.setAttribute("error", "Error loading checklist: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/staff/attendance_dashboard.jsp").forward(request, response);
        }
    }
}
