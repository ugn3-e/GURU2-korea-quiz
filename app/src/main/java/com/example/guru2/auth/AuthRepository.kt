package com.example.guru2.auth

class AuthRepository(
    private val dataSource: SQLiteAuthDataSource
) {

    fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean {
        return dataSource.signup(username, password, nickname, gender, age, country)
    }

    fun login(username: String, password: String): User? {
        return dataSource.login(username, password)
    }

    // ✅ userId = Long 으로 통일
    fun increaseSolvedCount(userId: Long) {
        dataSource.increaseSolvedCount(userId)
    }

    fun getSolvedCount(userId: Long): Int {
        return dataSource.getSolvedCount(userId)
    }
}
