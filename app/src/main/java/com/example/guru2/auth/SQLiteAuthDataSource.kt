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
            CREATE TABLE users (
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

    // 🔹 회원가입
    fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("nickname", nickname)
            put("gender", gender)
            put("age", age)
            put("country", country)
            put("solved_count", 0)
        }

        return db.insert("users", null, values) != -1L
    }

    // 🔹 로그인
    fun login(username: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                id = cursor.getInt(0),
                username = cursor.getString(1),
                password = cursor.getString(2),
                nickname = cursor.getString(3),
                gender = cursor.getString(4),
                age = cursor.getInt(5),
                country = cursor.getString(6),
                solvedCount = cursor.getInt(7)
            )
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    // 🔹 문제 푼 개수 증가 ⭐ 핵심
    fun increaseSolvedCount(userId: Int) {
        val db = writableDatabase
        db.execSQL(
            "UPDATE users SET solved_count = solved_count + 1 WHERE id = ?",
            arrayOf(userId)
        )
    }
}
