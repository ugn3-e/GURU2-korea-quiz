package com.example.guru2

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// 오답 전용 DB 매니저
// 신조어(slang) / 맞춤법(spelling) 오답을 별도 DB로 관리
// 날짜별 그룹화 + 중복 제거 조회 지원

class WrongDBManager(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // 오답 전용 DB 파일명
        private const val DB_NAME = "wrong_note.db"
        // DB 버전 (테이블 구조 변경 시 증가)
        private const val DB_VERSION = 2
    }

    //
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    // DB 버전 변경 시 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTables(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        createTables(db)
    }

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
            """.trimIndent()
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
            """.trimIndent()
        )
    }

    // ===============================
    // ✅ 신조어 오답 (중복 제거)
    // ===============================
    fun getSlangWrongGrouped(
        context: Context,
        slangDB: SlangDBManager
    ): List<WrongDateGroup> {

        val userId = UserManager.getUserId(context)
        val db = readableDatabase
        val map = linkedMapOf<String, MutableList<WrongItem>>()

        val cursor = db.rawQuery(
            """
            SELECT quiz_id,
                   date(wrong_time,'unixepoch') AS wrong_date,
                   MAX(wrong_time) AS last_time
            FROM slang_wrong
            WHERE user_id = ?
            GROUP BY quiz_id, wrong_date
            ORDER BY last_time DESC
            """.trimIndent(),
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            val quizId = cursor.getInt(0)
            val date = cursor.getString(1)

            val quiz = slangDB.getQuizById(quizId)
            if (quiz != null) {
                map.getOrPut(date) { mutableListOf() }
                    .add(WrongItem(quizId, quiz.slangWord))
            }
        }

        cursor.close()
        db.close()

        return map.map { WrongDateGroup(it.key, it.value) }
    }

    // ===============================
    // ✅ 맞춤법 오답 (중복 제거)
    // ===============================
    fun getSpellingWrongGrouped(
        context: Context,
        dbManager: SpellDBManager
    ): List<WrongDateGroup> {

        val userId = UserManager.getUserId(context)
        val db = readableDatabase
        val map = linkedMapOf<String, MutableList<WrongItem>>()

        val cursor = db.rawQuery(
            """
            SELECT quiz_id,
                   date(wrong_time,'unixepoch') AS wrong_date,
                   MAX(wrong_time) AS last_time
            FROM spelling_wrong
            WHERE user_id = ?
            GROUP BY quiz_id, wrong_date
            ORDER BY last_time DESC
            """.trimIndent(),
            arrayOf(userId)
        )

        while (cursor.moveToNext()) {
            val quizId = cursor.getInt(0)
            val date = cursor.getString(1)

            val quiz = dbManager.getQuizById(quizId)
            if (quiz != null) {
                map.getOrPut(date) { mutableListOf() }
                    .add(WrongItem(quizId, quiz.correct))
            }
        }

        cursor.close()
        db.close()

        return map.map { WrongDateGroup(it.key, it.value) }
    }

    // ===============================
    // 저장 로직 (그대로)
    // ===============================
    fun saveSlangWrong(context: Context, quizId: Int, userAnswer: String) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        val values = ContentValues().apply {
            put("user_id", userId)
            put("quiz_id", quizId)
            put("user_answer", userAnswer)
            put("wrong_time", System.currentTimeMillis() / 1000)
        }

        db.insert("slang_wrong", null, values)
        db.close()
    }

    fun saveSpellingWrong(context: Context, quizId: Int, userAnswer: String) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase

        val values = ContentValues().apply {
            put("user_id", userId)
            put("quiz_id", quizId)
            put("user_answer", userAnswer)
            put("wrong_time", System.currentTimeMillis() / 1000)
        }

        db.insert("spelling_wrong", null, values)
        db.close()
    }

    fun clearAllWrong(context: Context) {
        val userId = UserManager.getUserId(context)
        val db = writableDatabase
        db.execSQL("DELETE FROM slang_wrong WHERE user_id = ?", arrayOf(userId))
        db.execSQL("DELETE FROM spelling_wrong WHERE user_id = ?", arrayOf(userId))
        db.close()
    }
}
