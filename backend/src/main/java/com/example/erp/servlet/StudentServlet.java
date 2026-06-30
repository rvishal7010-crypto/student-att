package com.example.erp.servlet;

import com.example.erp.dao.StudentDAO;
import com.example.erp.model.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Controller servlet executing full CRUD actions for Student profiles.
 * Contains session validation and administrative privilege guard configurations.
 */
@WebServlet("/admin/students")
public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        this.studentDAO = new StudentDAO();
    }

    /**
     * Guard validator enforcing administrative login access.
     */
    private boolean checkAdminAccess(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null || 
            !"ADMIN".equals(session.getAttribute("userRole"))) {
            
            // Access Denied - Forward to unauthorized access page or login
            response.sendRedirect(request.getContextPath() + "/login?error=UnauthorizedAccess");
            return false;
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Ensure only authenticated Admins access this resource
        if (!checkAdminAccess(request, response)) return;

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showRegisterForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteStudentProfile(request, response);
                    break;
                case "search":
                    searchStudentRecords(request, response);
                    break;
                case "list":
                default:
                    listStudents(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!checkAdminAccess(request, response)) return;

        String action = request.getParameter("action");
        if (action == null) {
            action = "insert";
        }

        try {
            switch (action) {
                case "insert":
                    insertStudentProfile(request, response);
                    break;
                case "update":
                    updateStudentProfile(request, response);
                    break;
                default:
                    listStudents(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<Student> listStudents = studentDAO.getAllStudents();
        request.setAttribute("studentsList", listStudents);
        request.getRequestDispatcher("/WEB-INF/views/admin/students_list.jsp").forward(request, response);
    }

    private void searchStudentRecords(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("query");
        List<Student> listStudents;
        if (keyword != null && !keyword.trim().isEmpty()) {
            listStudents = studentDAO.searchStudents(keyword.trim());
        } else {
            listStudents = studentDAO.getAllStudents();
        }
        request.setAttribute("studentsList", listStudents);
        request.setAttribute("searchQuery", keyword);
        request.getRequestDispatcher("/WEB-INF/views/admin/students_list.jsp").forward(request, response);
    }

    private void showRegisterForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/student_form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        if (existingStudent != null) {
            request.setAttribute("student", existingStudent);
            request.getRequestDispatcher("/WEB-INF/views/admin/student_form.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/students?msg=StudentNotFound");
        }
    }

    private void insertStudentProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Retrieve inputs
        String name = request.getParameter("name");
        String rollNo = request.getParameter("rollNo");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String deptIdStr = request.getParameter("departmentId");
        String courseIdStr = request.getParameter("courseId");
        String semIdStr = request.getParameter("semesterId");
        String initPass = request.getParameter("password");

        // Server-side validation
        String validationError = null;
        if (name == null || name.trim().isEmpty()) validationError = "Student Name is required.";
        else if (rollNo == null || rollNo.trim().isEmpty()) validationError = "Roll Number is required.";
        else if (email == null || !email.contains("@")) validationError = "Valid Student Email is required.";
        else if (initPass == null || initPass.trim().length() < 6) validationError = "Temporary password must be at least 6 characters.";

        if (validationError != null) {
            request.setAttribute("error", validationError);
            request.getRequestDispatcher("/WEB-INF/views/admin/student_form.jsp").forward(request, response);
            return;
        }

        Student student = new Student();
        student.setName(name.trim());
        student.setRollNo(rollNo.trim().toUpperCase());
        student.setEmail(email.trim());
        student.setPhone(phone != null ? phone.trim() : "");
        student.setDepartmentId(Integer.parseInt(deptIdStr));
        student.setCourseId(Integer.parseInt(courseIdStr));
        student.setSemesterId(Integer.parseInt(semIdStr));

        boolean success = studentDAO.insertStudent(student, initPass);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/students?msg=RegisteredSuccessfully");
        } else {
            request.setAttribute("error", "Database Insertion Error: Could not register student (Roll No or Email might be duplicate).");
            request.getRequestDispatcher("/WEB-INF/views/admin/student_form.jsp").forward(request, response);
        }
    }

    private void updateStudentProfile(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        int departmentId = Integer.parseInt(request.getParameter("departmentId"));
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int semesterId = Integer.parseInt(request.getParameter("semesterId"));

        Student student = studentDAO.getStudentById(id);
        if (student != null) {
            student.setName(name);
            student.setEmail(email);
            student.setPhone(phone);
            student.setDepartmentId(departmentId);
            student.setCourseId(courseId);
            student.setSemesterId(semesterId);

            boolean success = studentDAO.updateStudent(student);
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/students?msg=UpdatedSuccessfully");
            } else {
                request.setAttribute("student", student);
                request.setAttribute("error", "Database Error: Could not update student details.");
                request.getRequestDispatcher("/WEB-INF/views/admin/student_form.jsp").forward(request, response);
            }
        }
    }

    private void deleteStudentProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean success = studentDAO.deleteStudent(id);
        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin/students?msg=DeletedSuccessfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/students?msg=DeleteFailed");
        }
    }
}
