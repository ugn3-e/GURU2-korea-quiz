package com.example.guru2.auth

// User 데이터 클래스
data class User(
    val id: Long,
    val username: String,
    val password: String,
    val nickname: String,
    val gender: String,
    val age: Int,
    val country: String,
    val solvedCount: Int // 푼 문제 수
)
