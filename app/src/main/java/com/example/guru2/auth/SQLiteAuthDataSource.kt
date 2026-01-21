package com.example.guru2.auth

import android.content.ContentValues
import android.content.Context
import com.example.guru2.db.AuthDBHelper

class SQLiteAuthDataSource(context: Context) : AuthDataSource {

    private val dbHelper = AuthDBHelper(context)

    override fun signup(
        username: String,
        password: String,
        nickname: String,
        gender: String,
        age: Int,
        country: String
    ): Boolean {

        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
            put("nickname", nickname)
            put("gender", gender)
            put("age", age)
            put("country", country)
        }

        return try {
            db.insertOrThrow("users", null, values)
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    override fun login(username: String, password: String): User? {

        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT id, username, nickname, gender, age, country
            FROM users
            WHERE username=? AND password=?
            """.trimIndent(),
            arrayOf(username, password)
        )

        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getLong(0),
                username = cursor.getString(1),
                nickname = cursor.getString(2),
                gender = cursor.getString(3),
                age = cursor.getInt(4),
                country = cursor.getString(5)
            )
        } else null

        cursor.close()
        db.close()
        return user
    }
}
