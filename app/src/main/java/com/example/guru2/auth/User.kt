package com.example.guru2.auth

data class User(
    val id: Long,
    val username: String,
    val nickname: String,
    val gender: String,
    val age: Int,
    val country: String
)
