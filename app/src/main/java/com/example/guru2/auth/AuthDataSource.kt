package com.example.guru2.auth

interface AuthDataSource {

    fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean

    fun login(username: String, password: String): User?
}
