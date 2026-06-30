package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rollNumber: String,
    val department: String,
    val semester: String,
    val profileColorHex: String = "#D0BCFF"
)

@Entity(tableName = "class_sessions")
data class ClassSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val subjectCode: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val date: String,
    val attendanceTaken: Boolean = false
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val classSessionId: Int,
    val studentId: Int,
    val isPresent: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "internal_marks")
data class InternalMarks(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentId: Int,
    val subjectCode: String,
    val test1: Int, // max 30
    val test2: Int, // max 30
    val assignment: Int, // max 10
    val attendanceScore: Int // max 30 (based on attendance percentage)
) {
    val totalMarks: Int
        get() = test1 + test2 + assignment + attendanceScore
}
