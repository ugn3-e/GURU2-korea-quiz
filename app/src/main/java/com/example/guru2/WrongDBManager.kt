package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 오답 전용 DB
// 신조어 문제 DB(slang_quiz.db), 맞춤법 DB(spelling_quiz.db)와 분리
class WrongDBManager(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "wrong_note.db"
        private const val DB_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 버전이 올라가도 테이블 항상 보장
        createTables(db)
    }

    // ⭐ 핵심 수정 ⭐
    // DB 파일이 이미 존재해도 테이블 생성 보장
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        createTables(db)
    }

    // =========================================================
    // 테이블 생성 (항상 안전)
    // =========================================================
    private fun createTables(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS slang_wrong (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT,
                quiz_id INTEGER,
                user_answer TEXT,
                wrong_time INTEGER
            )
            """
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS spelling_wrong (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT,
                quiz_id INTEGER,
                user_answer TEXT,
                wrong_time INTEGER
            )
            """
        )
    }

    // =========================================================
    // 공통 모델
    // =========================================================
    data class WrongQuiz(
        val quizId: Int,
        val question: String,
        val date: String
    )

    // =========================================================
    // 신조어 오답 저장
    // =========================================================
    fun saveSlangWrong(context: Context, quizId: Int, userAnswer: String) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        db.execSQL(
            "DELETE FROM slang_wrong WHERE user_id = ? AND quiz_id = ?",
            arrayOf(userId, quizId)
        )

        db.execSQL(
            """
            INSERT INTO slang_wrong (user_id, quiz_id, user_answer, wrong_time)
            VALUES (?, ?, ?, strftime('%s','now'))
            """,
            arrayOf(userId, quizId, userAnswer)
        )

        db.close()
    }

    // =========================================================
    // 맞춤법 오답 저장
    // =========================================================
    fun saveSpellingWrong(context: Context, quizId: Int, userAnswer: String) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        db.execSQL(
            "DELETE FROM spelling_wrong WHERE user_id = ? AND quiz_id = ?",
            arrayOf(userId, quizId)
        )

        db.execSQL(
            """
            INSERT INTO spelling_wrong (user_id, quiz_id, user_answer, wrong_time)
            VALUES (?, ?, ?, strftime('%s','now'))
            """,
            arrayOf(userId, quizId, userAnswer)
        )

        db.close()
    }

    // =========================================================
    // 신조어 오답 목록
    // =========================================================
    fun getSlangWrongList(
        context: Context,
        slangDB: SlangDBManager
    ): List<WrongQuiz> {

        val userId = UserManager.getUserId(context)
        val list = mutableListOf<WrongQuiz>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT quiz_id, date(wrong_time,'unixepoch')
            FROM slang_wrong
            WHERE user_id = ?
            GROUP BY quiz_id
            ORDER BY wrong_time DESC
            """,
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            val quizId = cursor.getInt(0)
            val date = cursor.getString(1)
            val quiz = slangDB.getQuizById(quizId)
            if (quiz != null) {
                list.add(WrongQuiz(quiz.id, quiz.question, date))
            }
        }

        cursor.close()
        db.close()
        return list
    }

    // =========================================================
    // 맞춤법 오답 목록
    // =========================================================
    fun getSpellingWrongList(
        context: Context,
        dbManager: DBManager
    ): List<WrongQuiz> {

        val userId = UserManager.getUserId(context)
        val list = mutableListOf<WrongQuiz>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT quiz_id, date(wrong_time,'unixepoch')
            FROM spelling_wrong
            WHERE user_id = ?
            GROUP BY quiz_id
            ORDER BY wrong_time DESC
            """,
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            val quizId = cursor.getInt(0)
            val date = cursor.getString(1)
            val quiz = dbManager.getQuizById(quizId)
            if (quiz != null) {
                list.add(WrongQuiz(quizId, quiz.sentence, date))
            }
        }

        cursor.close()
        db.close()
        return list
    }

    // =========================================================
    // 오답 전체 초기화 (기존 기능 유지)
    // =========================================================
    fun clearAllWrong(context: Context) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase
        db.execSQL("DELETE FROM slang_wrong WHERE user_id = ?", arrayOf(userId))
        db.execSQL("DELETE FROM spelling_wrong WHERE user_id = ?", arrayOf(userId))
        db.close()
    }
}
