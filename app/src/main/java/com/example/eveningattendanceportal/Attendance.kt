package com.example.eveningattendanceportal

data class Attendance(
    val rollNumber: String,
    val date: String,
    val status: String,
    val studentName: String = ""
)
