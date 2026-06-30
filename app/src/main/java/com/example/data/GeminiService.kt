package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateAttendanceReport(
        className: String,
        totalStudents: Int,
        averageAttendance: Double,
        lowAttendersList: List<String>,
        attendanceHistorySummary: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is missing or invalid. Please configure your GEMINI_API_KEY in the Secrets panel in AI Studio."
        }

        val prompt = """
            You are an expert academic counselor and director of a computer science department.
            Generate a highly professional, constructive, and actionable Attendance Analytics & Intervention Report for:
            
            - Class Name: $className
            - Total Registered Students: $totalStudents
            - Average Weekly Attendance: ${String.format("%.1f", averageAttendance)}%
            - Flagged Students (Attendance < 75%): ${if (lowAttendersList.isEmpty()) "None (All above threshold)" else lowAttendersList.joinToString(", ")}
            - Attendance History Context: $attendanceHistorySummary
            
            Please format your response into clean, readable sections:
            1. Executive Summary: Short analysis of the performance.
            2. High Risk Students & Impact: Brief consequence explanation (eligibility for exams, internals).
            3. Actionable Intervention Plan: 3 clear steps for the lecturer to improve attendance and support flagged students.
            
            Keep the tone encouraging, professional, academic, and concise. Do not use complex formatting; return plain text with clear headings and bullet points.
        """.trimIndent()

        try {
            val jsonRequest = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e("GeminiService", "API Error: Code ${response.code}, Body: $errBody")
                return@withContext "Failed to generate report. API Error: ${response.code}. Please ensure your API Key is valid."
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No content text generated.")
                    }
                }
            }
            return@withContext "Unexpected API response format."
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini", e)
            return@withContext "Error connecting to Gemini Service: ${e.localizedMessage ?: "Unknown error"}"
        }
    }

    suspend fun generateGenericContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is missing or invalid. Please configure your GEMINI_API_KEY in the Secrets panel in AI Studio."
        }

        try {
            val jsonRequest = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            val requestBody = jsonRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e("GeminiService", "API Error: Code ${response.code}, Body: $errBody")
                return@withContext "Failed to generate content. API Error: ${response.code}."
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                if (content != null) {
                    val parts = content.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "No content text generated.")
                    }
                }
            }
            return@withContext "Unexpected API response format."
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini", e)
            return@withContext "Error connecting to Gemini Service: ${e.localizedMessage ?: "Unknown error"}"
        }
    }
}
