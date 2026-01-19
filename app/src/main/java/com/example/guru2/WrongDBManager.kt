package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 오답 전용 DB
// 신조어 문제 DB(slang_quiz.db)와 분리
// 내부 DB -> 새로운 DB 생성 -> 쓰기 가능

class WrongDBManager(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {

        // 디미 유진_새로운 오답 DB 이름, 버전
        private const val DB_NAME = "wrong_note.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {

        // 디미 유진_오답 테이블 생성 (고유 id, 유저 아이디(로그인 염두), 퀴즈 id, 유저 답, 틀린 시간)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS slang_wrong (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT,
                quiz_id INTEGER,
                user_answer TEXT,
                wrong_time INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 디미 유진_db 요소 변경 없으니, 지금은 사용 안 함
    }

    // 디미 유진_ 오답 저장
    fun saveWrongQuiz(context: Context, quizId: Int, userAnswer: String) {

        // 디미 유진_현재 사용자 식별자 가져오기
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        db.execSQL(
            """
            INSERT INTO slang_wrong (user_id, quiz_id, user_answer, wrong_time)
            VALUES (?, ?, ?, strftime('%s','now'))
            """.trimIndent(),
            arrayOf(userId, quizId, userAnswer)
        )

        db.close()
    }

    // 디미 유진_오답에서 사용할 데이터
    data class WrongQuiz(
        val quizId: Int,
        val question: String
    )

    // 디미 유진_오답 목록 조회
    fun getWrongQuizList(
        context: Context,
        slangDB: SlangDBManager
    ): List<WrongQuiz> {

        val userId = UserManager.getUserId(context)
        val list = mutableListOf<WrongQuiz>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT quiz_id, MAX(wrong_time)
            FROM slang_wrong
            WHERE user_id = ?
            GROUP BY quiz_id
            ORDER BY MAX(wrong_time) DESC
            """.trimIndent(),
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            val quizId = cursor.getInt(0)
            val quiz = slangDB.getQuizById(quizId)

            // 디미 유진_문제 원본에서 정보 조회
            if (quiz != null) {
                list.add(
                    WrongQuiz(
                        quizId = quiz.id,
                        question = quiz.question
                    )
                )
            }
        }

        cursor.close()
        db.close()
        return list
    }

    // 디미 유진_개발자용 오답 초기화 (개발자는 계속 테스트하니까 오답 내역이 남아있게 된다)
    fun clearWrongForDebug(context: Context) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        db.execSQL(
            "DELETE FROM slang_wrong WHERE user_id = ?",
            arrayOf(userId)
        )

        db.close()
    }
}
