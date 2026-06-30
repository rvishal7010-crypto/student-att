-- ==========================================
-- SMARTATTEND COLLEGE ERP DATABASE SCHEMA
-- RDBMS: MySQL
-- Author: ERP Backend Architect
-- ==========================================

/*
================================================================================
ER DIAGRAM & RELATIONSHIP EXPLANATION
================================================================================

1. NORMALISED DATABASE ARCHITECTURE (3NF)
   - Every table contains atomic values. All non-key columns depend fully and 
     only on the primary key.
   - Separate master entities (Department, Course, Subject, Semester) eliminate
     redundant text replication and data anomalies.

2. CORE ENTITY RELATIONSHIPS:
   - UserLogin is the master authentication table. Admin, Student, and Staff tables 
     possess a 1-to-1 relationship with UserLogin via 'login_id' (Foreign Key).
   - Department holds a 1-to-many relationship with Course, Student, and Staff.
   - Course has a 1-to-many relationship with Subject.
   - Subject belongs to a specific Course and Semester (Composite/Single FK relationships).
   - Attendance bridges Student and Subject (many-to-many resolved via a join table) 
     and records status for specific dates, marked by a Staff entity.
   - InternalMarks holds test grades for a Student in a particular Subject.
   - Notifications represent announcements transmitted by a role (or system) and 
     targeted to target roles (e.g., ADMIN, STAFF, STUDENT).
   - Reports represent system-generated or AI-generated statistical evaluations.

3. INTEGRITY & CONSTRAINTS:
   - PRIMARY KEYS: Auto-incrementing integers ensure high-performance indexing and unique row identification.
   - FOREIGN KEYS: Cascade restrictions/on-delete-null ensure absolute referential integrity across master-detail deletions.
   - UNIQUE KEYS: Roll numbers, Employee IDs, and Usernames have unique constraints to prevent duplication.
*/

-- Create Database if not exists
CREATE DATABASE IF NOT EXISTS smart_attend_db;
USE smart_attend_db;

-- -----------------------------------------------------
-- 1. Table `UserLogin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `UserLogin` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL, -- Recommended: SHA-256 Hash
  `role` ENUM('ADMIN', 'STAFF', 'STUDENT') NOT NULL,
  `is_active` TINYINT(1) DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 2. Table `Department`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Department` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `code` VARCHAR(10) NOT NULL UNIQUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 3. Table `Admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Admin` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `login_id` INT NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` VARCHAR(15),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`login_id`) REFERENCES `UserLogin`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 4. Table `Course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Course` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(15) NOT NULL UNIQUE,
  `department_id` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`department_id`) REFERENCES `Department`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 5. Table `Semester`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Semester` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `number` INT NOT NULL UNIQUE, -- e.g., 1, 2, 3, 4, 5, 6, 7, 8
  `academic_year` VARCHAR(15) NOT NULL -- e.g., "2025-2026"
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 6. Table `Subject`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Subject` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(100) NOT NULL,
  `code` VARCHAR(15) NOT NULL UNIQUE,
  `course_id` INT NOT NULL,
  `semester_id` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`course_id`) REFERENCES `Course`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`semester_id`) REFERENCES `Semester`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 7. Table `Student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Student` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `login_id` INT NOT NULL UNIQUE,
  `roll_no` VARCHAR(20) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` VARCHAR(15),
  `department_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `semester_id` INT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`login_id`) REFERENCES `UserLogin`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`department_id`) REFERENCES `Department`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`course_id`) REFERENCES `Course`(`id`) ON DELETE RESTRICT,
  FOREIGN KEY (`semester_id`) REFERENCES `Semester`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 8. Table `Staff`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Staff` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `login_id` INT NOT NULL UNIQUE,
  `employee_id` VARCHAR(20) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone` VARCHAR(15),
  `department_id` INT NOT NULL,
  `designation` VARCHAR(100) NOT NULL, -- e.g., "Senior Lecturer", "HOD", "Professor"
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`login_id`) REFERENCES `UserLogin`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`department_id`) REFERENCES `Department`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 9. Table `Attendance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Attendance` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `subject_id` INT NOT NULL,
  `date` DATE NOT NULL,
  `status` ENUM('PRESENT', 'ABSENT') NOT NULL,
  `marked_by` INT NOT NULL, -- refers to Staff.id
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_student_subject_date` (`student_id`, `subject_id`, `date`), -- Prevents duplicate entries per student for same class session
  FOREIGN KEY (`student_id`) REFERENCES `Student`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`subject_id`) REFERENCES `Subject`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`marked_by`) REFERENCES `Staff`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 10. Table `InternalMarks`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `InternalMarks` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `student_id` INT NOT NULL,
  `subject_id` INT NOT NULL,
  `cia1` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 50
  `cia2` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 50
  `model_exam` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 100
  `assignment` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 10
  `lab` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 50
  `seminar` DECIMAL(5,2) DEFAULT 0.00, -- Maximum 20
  `total` DECIMAL(5,2) DEFAULT 0.00, -- Automatically calculated and saved via application layer
  `average` DECIMAL(5,2) DEFAULT 0.00, -- Calculated as total / 6.0
  `percentage` DECIMAL(5,2) DEFAULT 0.00, -- Calculated as (total / 280) * 100
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_student_subject_marks` (`student_id`, `subject_id`),
  FOREIGN KEY (`student_id`) REFERENCES `Student`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`subject_id`) REFERENCES `Subject`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 11. Table `Notifications`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Notifications` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(150) NOT NULL,
  `message` TEXT NOT NULL,
  `sender_role` ENUM('ADMIN', 'STAFF', 'SYSTEM') NOT NULL,
  `target_role` ENUM('ALL', 'ADMIN', 'STAFF', 'STUDENT') NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------
-- 12. Table `Reports`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Reports` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(150) NOT NULL,
  `description` TEXT,
  `generated_by` VARCHAR(100) NOT NULL, -- Name or System context
  `report_type` VARCHAR(50) NOT NULL, -- e.g., "ATTENDANCE_AGGREGATE", "GENIMINI_AI_INTERVENTION"
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- SEED DATA & MOCK RECORDS
-- =============================================================================

-- Seed UserLogin Roles (Passwords are plaintext "password123" for demo/reference)
INSERT INTO `UserLogin` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin_user', 'password123', 'ADMIN'),
(2, 'staff_user', 'password123', 'STAFF'),
(3, 'student_user', 'password123', 'STUDENT');

-- Seed Departments
INSERT INTO `Department` (`id`, `name`, `code`) VALUES
(1, 'Computer Science Engineering', 'CSE'),
(2, 'Information Technology', 'IT');

-- Seed Admins
INSERT INTO `Admin` (`login_id`, `name`, `email`, `phone`) VALUES
(1, 'Dean Michael Vance', 'dean.vance@college.edu', '+15550199');

-- Seed Courses
INSERT INTO `Course` (`id`, `name`, `code`, `department_id`) VALUES
(1, 'B.Tech Computer Science', 'BTECH-CSE', 1);

-- Seed Semesters
INSERT INTO `Semester` (`id`, `number`, `academic_year`) VALUES
(1, 8, '2025-2026'),
(2, 7, '2025-2026');

-- Seed Subjects
INSERT INTO `Subject` (`id`, `name`, `code`, `course_id`, `semester_id`) VALUES
(1, 'Java Full Stack Dev', 'CSE-801', 1, 1),
(2, 'Machine Learning', 'CSE-802', 1, 1),
(3, 'Cloud Computing', 'CSE-803', 1, 1);

-- Seed Students
INSERT INTO `Student` (`id`, `login_id`, `roll_no`, `name`, `email`, `phone`, `department_id`, `course_id`, `semester_id`) VALUES
(1, 3, 'CSE-2026-001', 'John Doe', 'john.doe@student.edu', '+15550201', 1, 1, 1);

-- Seed Staff members
INSERT INTO `Staff` (`id`, `login_id`, `employee_id`, `name`, `email`, `phone`, `department_id`, `designation`) VALUES
(1, 2, 'EMP-2026-042', 'Dr. Sarah Conner', 'sarah.conner@college.edu', '+15550302', 1, 'Senior Lecturer');

-- Seed Attendance Records
INSERT INTO `Attendance` (`student_id`, `subject_id`, `date`, `status`, `marked_by`) VALUES
(1, 1, '2026-06-29', 'PRESENT', 1),
(1, 2, '2026-06-29', 'ABSENT', 1);

-- Seed InternalMarks Records
INSERT INTO `InternalMarks` (`student_id`, `subject_id`, `cia1`, `cia2`, `model_exam`, `assignment`, `lab`, `seminar`, `total`, `average`, `percentage`) VALUES
(1, 1, 42.00, 45.00, 85.00, 9.00, 44.00, 18.00, 243.00, 40.50, 86.79),
(1, 2, 35.00, 38.00, 72.00, 8.00, 40.00, 15.00, 208.00, 34.67, 74.29);

-- Seed Notifications
INSERT INTO `Notifications` (`title`, `message`, `sender_role`, `target_role`) VALUES
( 'Upcoming End Term Exams', 'Theory examinations are set to commence from July 15, 2026.', 'ADMIN', 'ALL' );

-- Seed Reports
INSERT INTO `Reports` (`title`, `description`, `generated_by`, `report_type`) VALUES
('End Term Attendance Summary', 'Analysis of attendance averages for CSE Semester 8 Students.', 'System Auditor', 'ATTENDANCE_AGGREGATE');
