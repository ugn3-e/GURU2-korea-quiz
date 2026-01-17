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
            sentence text, 
            choice1 text, choice2 text, choice3 text, 
            correct text, 
            correct_exp text,
            incorrect_exp text,
            source text)""".trimIndent())

        //샘플 데이터
        db.execSQL("""
            INSERT INTO spelling_quiz
            VALUES(null, '그건 절대 _____, 내일 다시 해보자.', '안 돼', '안 되', '않되', '안 돼',
                    '해당 영상은 2017년에 개봉한 한국영화 기생충에 나오는 대사로, 주인공 기우가 ~~~~ 상황에 제자 다혜에게 사용한 말이다.',
                    '해설: 되다 의 부정은 안 + 되다이므로 안 돼(=안 되다) 처럼 띄어서 사용합니다.', 
                    '영화<기생충>') 
        """.trimIndent())
        db.execSQL("""
            INSERT INTO spelling_quiz
            VALUES(null, '____가 없네.', '어의', '어위', '어이', '어이',
                    '해당 영상은 2015년에 개봉한 한국영화 베테랑에서 나오는 대사로, 조태오가 ~~~ 상황에서 사용한 말이다.',
                    '해설: 어이없다 는 일이 너무 뜻밖이어서 기가 막힌 상황에 쓰는 말입니다.', 
                    '영화<베테랑>')
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

    fun getQuizById(id: Int) : QuizData? {
        val db = this.readableDatabase
        var cursor = db.rawQuery("SELECT * FROM spelling_quiz WHERE id = ?", arrayOf(id.toString()))

        var quiz: QuizData? = null
        if(cursor.moveToFirst()) {
            quiz = QuizData(
                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow(("incorrect_exp"))) // ☑️ 오답 추가
            )
        }
        cursor.close()
        return quiz
    }
}
data class QuizData(val sentence: String, val correct: String, val correct_exp: String, val incorrect_exp: String)