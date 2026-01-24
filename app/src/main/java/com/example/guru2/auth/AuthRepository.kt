package com.example.guru2.auth

import com.example.guru2.db.SQLiteAuthDataSource

// 중간 관리 역할
class AuthRepository(
    private val dataSource: SQLiteAuthDataSource
) {

    // 회원가입
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

    // 로그인
    fun login(username: String, password: String): User? {
        return dataSource.login(username, password)
    }

    // 푼 문제 수
    fun increaseSolvedCount(userId: Long) {
        dataSource.increaseSolvedCount(userId)
    }

    fun getSolvedCount(userId: Long): Int {
        return dataSource.getSolvedCount(userId)
    }

    // 사용자 닉네임
    fun getNickname(userId: Long): String {
        return dataSource.getNickname(userId)
    }
}
