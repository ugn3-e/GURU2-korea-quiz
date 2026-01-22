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

    fun increaseSolvedCount(userId: Int) {
        dataSource.increaseSolvedCount(userId)
    }
}
