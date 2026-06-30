package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class AttendanceRepository(private val dao: AttendanceDao) {

    val allStudentsFlow: Flow<List<Student>> = dao.getAllStudentsFlow()
    val allClassSessionsFlow: Flow<List<ClassSession>> = dao.getAllClassSessionsFlow()
    val allMarksFlow: Flow<List<InternalMarks>> = dao.getAllMarksFlow()

    suspend fun getAllStudents(): List<Student> = dao.getAllStudents()
    suspend fun getAllClassSessions(): List<ClassSession> = dao.getAllClassSessions()
    suspend fun getClassSessionById(id: Int): ClassSession? = dao.getClassSessionById(id)

    suspend fun insertStudent(student: Student): Long = dao.insertStudent(student)
    suspend fun deleteStudent(student: Student) = dao.deleteStudent(student)

    suspend fun insertClassSession(session: ClassSession): Long = dao.insertClassSession(session)
    suspend fun updateClassSession(session: ClassSession) = dao.updateClassSession(session)
    suspend fun deleteClassSession(session: ClassSession) = dao.deleteClassSession(session)

    fun getAttendanceForSessionFlow(sessionId: Int): Flow<List<AttendanceRecord>> = dao.getAttendanceForSessionFlow(sessionId)
    suspend fun getAttendanceForSession(sessionId: Int): List<AttendanceRecord> = dao.getAttendanceForSession(sessionId)
    suspend fun getAttendanceForStudent(studentId: Int): List<AttendanceRecord> = dao.getAttendanceForStudent(studentId)
    suspend fun getAllAttendanceRecords(): List<AttendanceRecord> = dao.getAllAttendanceRecords()

    suspend fun saveAttendance(sessionId: Int, records: List<AttendanceRecord>) {
        dao.deleteAttendanceForSession(sessionId)
        dao.insertAttendanceRecords(records)
        val session = dao.getClassSessionById(sessionId)
        if (session != null) {
            dao.updateClassSession(session.copy(attendanceTaken = true))
        }
    }

    suspend fun getMarksForStudent(studentId: Int): List<InternalMarks> = dao.getMarksForStudent(studentId)
    suspend fun insertInternalMarks(marks: InternalMarks): Long = dao.insertInternalMarks(marks)

    suspend fun prePopulateIfEmpty() {
        val students = dao.getAllStudents()
        if (students.isEmpty()) {
            // Seed Students
            val seededStudents = listOf(
                Student(name = "John Doe", rollNumber = "CSE-2026-001", department = "CSE", semester = "Sem 8", profileColorHex = "#FFD8E4"),
                Student(name = "Sarah Khan", rollNumber = "CSE-2026-002", department = "CSE", semester = "Sem 8", profileColorHex = "#D0BCFF"),
                Student(name = "Alex Mercer", rollNumber = "CSE-2026-003", department = "CSE", semester = "Sem 8", profileColorHex = "#C2E7FF"),
                Student(name = "Ram Vishal", rollNumber = "CSE-2026-004", department = "CSE", semester = "Sem 8", profileColorHex = "#FFF0AA"),
                Student(name = "Priya Lakshmi", rollNumber = "CSE-2026-005", department = "CSE", semester = "Sem 8", profileColorHex = "#C8F7C5"),
                Student(name = "Marcus Brown", rollNumber = "CSE-2026-006", department = "CSE", semester = "Sem 8", profileColorHex = "#FFD8BE")
            )
            val studentIds = mutableListOf<Int>()
            for (st in seededStudents) {
                val id = dao.insertStudent(st)
                studentIds.add(id.toInt())
            }

            // Seed Sessions
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            val todayStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateFormat.format(calendar.time)

            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val twoDaysAgoStr = dateFormat.format(calendar.time)

            val session1Id = dao.insertClassSession(
                ClassSession(
                    subjectName = "Java Full Stack Dev",
                    subjectCode = "CSE-801",
                    room = "Lab Room 12B",
                    startTime = "09:30 AM",
                    endTime = "11:30 AM",
                    date = todayStr,
                    attendanceTaken = false
                )
            ).toInt()

            val session2Id = dao.insertClassSession(
                ClassSession(
                    subjectName = "Machine Learning",
                    subjectCode = "CSE-802",
                    room = "Lecture Hall A",
                    startTime = "11:30 AM",
                    endTime = "01:00 PM",
                    date = yesterdayStr,
                    attendanceTaken = true
                )
            ).toInt()

            val session3Id = dao.insertClassSession(
                ClassSession(
                    subjectName = "Cloud Computing",
                    subjectCode = "CSE-803",
                    room = "Seminar Room 3",
                    startTime = "02:00 PM",
                    endTime = "03:30 PM",
                    date = twoDaysAgoStr,
                    attendanceTaken = true
                )
            ).toInt()

            // Seed Attendance for session 2 & 3
            val session2Attendance = listOf(
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[0], isPresent = true),
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[1], isPresent = true),
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[2], isPresent = false),
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[3], isPresent = true),
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[4], isPresent = true),
                AttendanceRecord(classSessionId = session2Id, studentId = studentIds[5], isPresent = true)
            )
            dao.insertAttendanceRecords(session2Attendance)

            val session3Attendance = listOf(
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[0], isPresent = true),
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[1], isPresent = true),
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[2], isPresent = true),
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[3], isPresent = true),
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[4], isPresent = false),
                AttendanceRecord(classSessionId = session3Id, studentId = studentIds[5], isPresent = true)
            )
            dao.insertAttendanceRecords(session3Attendance)

            // Seed internal marks
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[0], subjectCode = "CSE-801", test1 = 26, test2 = 25, assignment = 9, attendanceScore = 28))
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[1], subjectCode = "CSE-801", test1 = 28, test2 = 29, assignment = 10, attendanceScore = 30))
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[2], subjectCode = "CSE-801", test1 = 20, test2 = 18, assignment = 8, attendanceScore = 24))
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[3], subjectCode = "CSE-801", test1 = 25, test2 = 24, assignment = 9, attendanceScore = 28))
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[4], subjectCode = "CSE-801", test1 = 22, test2 = 21, assignment = 8, attendanceScore = 26))
            dao.insertInternalMarks(InternalMarks(studentId = studentIds[5], subjectCode = "CSE-801", test1 = 27, test2 = 26, assignment = 9, attendanceScore = 29))
        }
    }
}
