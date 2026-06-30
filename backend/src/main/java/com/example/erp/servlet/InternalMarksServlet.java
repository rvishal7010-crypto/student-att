package com.example.erp.servlet;

import com.example.erp.dao.AttendanceDAO;
import com.example.erp.dao.InternalMarksDAO;
import com.example.erp.dao.StaffDAO;
import com.example.erp.model.InternalMarks;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller servlet handling continuous evaluation score tracking and management.
 */
@WebServlet("/staff/marks")
public class InternalMarksServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private InternalMarksDAO marksDAO;
    private AttendanceDAO attendanceDAO;
    private StaffDAO staffDAO;

    @Override
    public void init() throws ServletException {
        this.marksDAO = new InternalMarksDAO();
        this.attendanceDAO = new AttendanceDAO();
        this.staffDAO = new StaffDAO();
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
        if ("retrieve".equals(action)) {
            retrieveStudentMarksList(request, response);
        } else {
            // Render default select subject view
            request.getRequestDispatcher("/WEB-INF/views/staff/internal_marks.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkStaffAccess(request, response)) return;

        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            String[] studentIdStrings = request.getParameterValues("studentIds");

            if (studentIdStrings != null) {
                boolean allSaved = true;

                for (String sIdStr : studentIdStrings) {
                    int studentId = Integer.parseInt(sIdStr);
                    
                    // Retrieve inputs for this specific student
                    String cia1Str = request.getParameter("cia1_" + studentId);
                    String cia2Str = request.getParameter("cia2_" + studentId);
                    String modelStr = request.getParameter("modelExam_" + studentId);
                    String assignmentStr = request.getParameter("assignment_" + studentId);
                    String labStr = request.getParameter("lab_" + studentId);
                    String seminarStr = request.getParameter("seminar_" + studentId);

                    double cia1 = cia1Str != null && !cia1Str.isEmpty() ? Double.parseDouble(cia1Str) : 0.0;
                    double cia2 = cia2Str != null && !cia2Str.isEmpty() ? Double.parseDouble(cia2Str) : 0.0;
                    double modelExam = modelStr != null && !modelStr.isEmpty() ? Double.parseDouble(modelStr) : 0.0;
                    double assignment = assignmentStr != null && !assignmentStr.isEmpty() ? Double.parseDouble(assignmentStr) : 0.0;
                    double lab = labStr != null && !labStr.isEmpty() ? Double.parseDouble(labStr) : 0.0;
                    double seminar = seminarStr != null && !seminarStr.isEmpty() ? Double.parseDouble(seminarStr) : 0.0;

                    InternalMarks marks = new InternalMarks();
                    marks.setStudentId(studentId);
                    marks.setSubjectId(subjectId);
                    marks.setCia1(cia1);
                    marks.setCia2(cia2);
                    marks.setModelExam(modelExam);
                    marks.setAssignment(assignment);
                    marks.setLab(lab);
                    marks.setSeminar(seminar);

                    if (marks.isValid()) {
                        boolean saved = marksDAO.saveOrUpdateMarks(marks);
                        if (!saved) allSaved = false;
                    } else {
                        allSaved = false;
                    }
                }

                if (allSaved) {
                    response.sendRedirect(request.getContextPath() + "/staff/marks?action=retrieve&subjectId=" + subjectId + "&msg=MarksSavedSuccessfully");
                } else {
                    response.sendRedirect(request.getContextPath() + "/staff/marks?action=retrieve&subjectId=" + subjectId + "&error=SomeMarksInvalidOrNotSaved");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/marks?msg=NoStudentsToEvaluate");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error saving internal marks: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/staff/internal_marks.jsp").forward(request, response);
        }
    }

    private void retrieveStudentMarksList(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            
            // Re-use attendance checklist loading which fetches students in the same class/subject
            List<Student> studentList = attendanceDAO.getStudentsForMarking(subjectId);
            
            // Map to store existing marks statement for each student
            Map<Integer, InternalMarks> marksMap = new HashMap<>();
            for (Student student : studentList) {
                InternalMarks existingMarks = marksDAO.getStudentMarksForSubject(student.getId(), subjectId);
                if (existingMarks != null) {
                    marksMap.put(student.getId(), existingMarks);
                } else {
                    // Seed empty object so getters do not fail
                    marksMap.put(student.getId(), new InternalMarks(0, student.getId(), subjectId, 0, 0, 0, 0, 0, 0));
                }
            }

            request.setAttribute("studentList", studentList);
            request.setAttribute("marksMap", marksMap);
            request.setAttribute("subjectId", subjectId);
            request.setAttribute("marksLoaded", true);

            request.getRequestDispatcher("/WEB-INF/views/staff/internal_marks.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error loading student marks list: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/staff/internal_marks.jsp").forward(request, response);
        }
    }
}
