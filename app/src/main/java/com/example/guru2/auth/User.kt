package com.example.guru2.auth

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val nickname: String,
    val gender: String,
    val age: Int,
    val country: String,
    val solvedCount: Int   // ⭐ 추가
)
