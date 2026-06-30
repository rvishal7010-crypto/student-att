package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    // Student operations
    @Query("SELECT * FROM students ORDER BY rollNumber ASC")
    fun getAllStudentsFlow(): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY rollNumber ASC")
    suspend fun getAllStudents(): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Delete
    suspend fun deleteStudent(student: Student)

    // Class Session operations
    @Query("SELECT * FROM class_sessions ORDER BY date DESC, startTime DESC")
    fun getAllClassSessionsFlow(): Flow<List<ClassSession>>

    @Query("SELECT * FROM class_sessions ORDER BY date DESC, startTime DESC")
    suspend fun getAllClassSessions(): List<ClassSession>

    @Query("SELECT * FROM class_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getClassSessionById(sessionId: Int): ClassSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassSession(session: ClassSession): Long

    @Update
    suspend fun updateClassSession(session: ClassSession)

    @Delete
    suspend fun deleteClassSession(session: ClassSession)

    // Attendance Record operations
    @Query("SELECT * FROM attendance_records WHERE classSessionId = :sessionId")
    fun getAttendanceForSessionFlow(sessionId: Int): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE classSessionId = :sessionId")
    suspend fun getAttendanceForSession(sessionId: Int): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    suspend fun getAttendanceForStudent(studentId: Int): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records")
    suspend fun getAllAttendanceRecords(): List<AttendanceRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecord>)

    @Query("DELETE FROM attendance_records WHERE classSessionId = :sessionId")
    suspend fun deleteAttendanceForSession(sessionId: Int)

    // Internal Marks operations
    @Query("SELECT * FROM internal_marks WHERE studentId = :studentId")
    suspend fun getMarksForStudent(studentId: Int): List<InternalMarks>

    @Query("SELECT * FROM internal_marks")
    fun getAllMarksFlow(): Flow<List<InternalMarks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternalMarks(marks: InternalMarks): Long
}
