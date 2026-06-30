package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class UserRole {
    ADMIN, STAFF, STUDENT
}

enum class ActiveTab {
    HOME, REPORT, STUDENTS, SETTINGS
}

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AttendanceRepository(db.attendanceDao())
    private val geminiService = GeminiService()

    // Configuration / Roles
    val currentRole = MutableStateFlow(UserRole.STAFF)
    val currentTab = MutableStateFlow(ActiveTab.HOME)
    val selectedStudentIdForStudentView = MutableStateFlow<Int?>(null)

    // Data Flow
    val students = repository.allStudentsFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val classSessions = repository.allClassSessionsFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val internalMarks = repository.allMarksFlow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // UI States for Active Attendance Marking
    val activeMarkingSessionId = MutableStateFlow<Int?>(null)
    private val _attendanceMarkingMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap()) // StudentId -> isPresent
    val attendanceMarkingMap = _attendanceMarkingMap.asStateFlow()

    // AI Report Generation State
    val aiReportText = MutableStateFlow("")
    val isGeneratingReport = MutableStateFlow(false)

    // Statistics Derived States
    val statistics = combine(students, classSessions) { studentList, sessionList ->
        val totalSessionsWithAttendance = sessionList.count { it.attendanceTaken }
        if (studentList.isEmpty() || totalSessionsWithAttendance == 0) {
            return@combine Stats(
                averageWeeklyAttendance = 94.2, // template default
                flaggedStudentsCount = 0,
                flaggedStudents = emptyList(),
                attendanceMap = emptyMap()
            )
        }

        // Calculate attendance percent for each student
        val records = repository.getAllAttendanceRecords()
        val studentAttendancePercent = studentList.associate { student ->
            val totalSessionsForStudent = totalSessionsWithAttendance
            val presentCount = records.count { it.studentId == student.id && it.isPresent }
            val pct = if (totalSessionsForStudent > 0) {
                (presentCount.toDouble() / totalSessionsForStudent) * 100.0
            } else {
                100.0
            }
            student to pct
        }

        val lowAttenders = studentAttendancePercent.filter { it.value < 75.0 }.keys.toList()
        val avgPct = studentAttendancePercent.values.average()

        Stats(
            averageWeeklyAttendance = if (avgPct.isNaN()) 94.2 else avgPct,
            flaggedStudentsCount = lowAttenders.size,
            flaggedStudents = lowAttenders,
            attendanceMap = studentAttendancePercent.mapKeys { it.key.id }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, Stats(94.2, 0, emptyList(), emptyMap()))

    init {
        viewModelScope.launch {
            repository.prePopulateIfEmpty()
            // Set default selected student for testing student view
            val firstStudent = repository.getAllStudents().firstOrNull()
            if (firstStudent != null) {
                selectedStudentIdForStudentView.value = firstStudent.id
            }
        }
    }

    // Attendance marking actions
    fun startMarkingAttendance(sessionId: Int) {
        viewModelScope.launch {
            activeMarkingSessionId.value = sessionId
            val currentRecords = repository.getAttendanceForSession(sessionId)
            val allSts = repository.getAllStudents()
            
            val initialMap = allSts.associate { student ->
                val existingRecord = currentRecords.find { it.studentId == student.id }
                student.id to (existingRecord?.isPresent ?: true) // default to present
            }
            _attendanceMarkingMap.value = initialMap
        }
    }

    fun toggleStudentAttendance(studentId: Int) {
        val currentMap = _attendanceMarkingMap.value.toMutableMap()
        currentMap[studentId] = !(currentMap[studentId] ?: true)
        _attendanceMarkingMap.value = currentMap
    }

    fun submitAttendance() {
        val sessionId = activeMarkingSessionId.value ?: return
        val currentMap = _attendanceMarkingMap.value
        
        viewModelScope.launch {
            val records = currentMap.map { (studentId, isPresent) ->
                AttendanceRecord(classSessionId = sessionId, studentId = studentId, isPresent = isPresent)
            }
            repository.saveAttendance(sessionId, records)
            activeMarkingSessionId.value = null
            _attendanceMarkingMap.value = emptyMap()
        }
    }

    fun cancelMarkingAttendance() {
        activeMarkingSessionId.value = null
        _attendanceMarkingMap.value = emptyMap()
    }

    // Student administration
    fun addNewStudent(name: String, rollNumber: String, department: String, semester: String) {
        viewModelScope.launch {
            val colors = listOf("#FFD8E4", "#D0BCFF", "#C2E7FF", "#FFF0AA", "#C8F7C5", "#FFD8BE")
            val randomColor = colors.random()
            val student = Student(
                name = name,
                rollNumber = rollNumber,
                department = department,
                semester = semester,
                profileColorHex = randomColor
            )
            repository.insertStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    // Class Session administration
    fun addNewClassSession(subjectName: String, subjectCode: String, room: String, startTime: String, endTime: String, date: String) {
        viewModelScope.launch {
            val session = ClassSession(
                subjectName = subjectName,
                subjectCode = subjectCode,
                room = room,
                startTime = startTime,
                endTime = endTime,
                date = date,
                attendanceTaken = false
            )
            repository.insertClassSession(session)
        }
    }

    fun deleteClassSession(session: ClassSession) {
        viewModelScope.launch {
            repository.deleteClassSession(session)
        }
    }

    // Internal marks updating
    fun updateInternalMarks(studentId: Int, subjectCode: String, test1: Int, test2: Int, assignment: Int, attendanceScore: Int) {
        viewModelScope.launch {
            val marks = InternalMarks(
                studentId = studentId,
                subjectCode = subjectCode,
                test1 = test1,
                test2 = test2,
                assignment = assignment,
                attendanceScore = attendanceScore
            )
            repository.insertInternalMarks(marks)
        }
    }

    // AI report generation
    fun generateAIReport(className: String) {
        viewModelScope.launch {
            isGeneratingReport.value = true
            aiReportText.value = ""
            
            val totalSts = students.value.size
            val avgAttendance = statistics.value.averageWeeklyAttendance
            val lowAttendersNames = statistics.value.flaggedStudents.map { it.name }
            
            val historySummary = classSessions.value
                .take(5)
                .joinToString("; ") { "${it.subjectName} on ${it.date} (Status: ${if (it.attendanceTaken) "Taken" else "Pending"})" }

            val result = geminiService.generateAttendanceReport(
                className = className,
                totalStudents = totalSts,
                averageAttendance = avgAttendance,
                lowAttendersList = lowAttendersNames,
                attendanceHistorySummary = historySummary
            )
            aiReportText.value = result
            isGeneratingReport.value = false
        }
    }
}

data class Stats(
    val averageWeeklyAttendance: Double,
    val flaggedStudentsCount: Int,
    val flaggedStudents: List<Student>,
    val attendanceMap: Map<Int, Double> // studentId -> AttendancePercent
)
