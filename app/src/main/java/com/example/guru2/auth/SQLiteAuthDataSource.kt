package com.example.guru2.auth

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteAuthDataSource(context: Context)
    : SQLiteOpenHelper(context, "auth.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                password TEXT,
                nickname TEXT,
                gender TEXT,
                age INTEGER,
                country TEXT,
                solved_count INTEGER DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    // 회원가입
    fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean {
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("nickname", nickname)
            put("gender", gender)
            put("age", age)
            put("country", country)
            put("solved_count", 0)
        }

        return writableDatabase.insert("users", null, values) != -1L
    }

    // 로그인
    fun login(username: String, password: String): User? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        val user =
            if (cursor.moveToFirst()) {
                User(
                    id = cursor.getLong(0),       // Long 타입
                    username = cursor.getString(1),
                    password = cursor.getString(2),
                    nickname = cursor.getString(3),
                    gender = cursor.getString(4),
                    age = cursor.getInt(5),
                    country = cursor.getString(6),
                    solvedCount = cursor.getInt(7)
                )
            } else null

        cursor.close()
        return user
    }

    // 문제 푼 개수 증가
    fun increaseSolvedCount(userId: Long) {   //  Long 타입
        writableDatabase.execSQL(
            """
            UPDATE users
            SET solved_count = solved_count + 1
            WHERE id = ?
            """.trimIndent(),
            arrayOf(userId)
        )
    }

    // solved_count 조회
    fun getSolvedCount(userId: Long): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT solved_count FROM users WHERE id = ?",
            arrayOf(userId.toString())
        )

        val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return count
    }

    // 닉네임 얻기
    fun getNickname(userId: Long): String {
        val db = readableDatabase
        var nickname = "슈니" // 기본값
        val cursor = db.rawQuery("SELECT nickname FROM users WHERE id = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            nickname = cursor.getString(0)
        }
        cursor.close()
        return nickname
    }
}
