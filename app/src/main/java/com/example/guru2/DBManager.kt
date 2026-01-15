package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {

        db!!.execSQL("""CREATE TABLE spelling_quiz (id INTEGER PRIMARY KEY AUTOINCREMENT,
            sentence text, choice1 text, choice2 text, choice3 text, correct text, explanation text, source text)""".trimIndent())

        //샘플 데이터
        db!!.execSQL("""
            INSERT INTO spelling_quiz
            VALUES(null, '그건 절대 _____, 내일 다시 해보자.', '안 돼', '안 되', '않되', '안 돼',
                    '해설: 되다 의 부정은 안 + 되다이므로 안 돼(=안 되다) 처럼 띄어서 사용합니다.', '영화<기생충>') 
        """.trimIndent())
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db!!.execSQL("DROP TABLE IF EXISTS spelling_quiz")
        onCreate(db)
    }
}