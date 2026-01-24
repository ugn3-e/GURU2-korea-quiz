package com.example.guru2.auth

interface AuthDataSource {

    // 회원가입
    fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean

    // 로그인
    fun login(username: String, password: String): User?
}
