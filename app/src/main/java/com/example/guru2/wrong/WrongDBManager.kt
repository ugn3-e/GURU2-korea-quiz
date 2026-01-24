package com.example.guru2.wrong

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru2.slangquiz.SlangDBManager
import com.example.guru2.spellquiz.SpellDBManager
import com.example.guru2.auth.UserManager

// 오답 전용 DB 매니저
// 신조어 / 맞춤법 오답을 별도 DB로 관리
// 날짜별 그룹화 + 중복 제거

class WrongDBManager(context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {

        // 오답 전용 DB 파일
        private const val DB_NAME = "wrong_note.db"

        // DB 버전 (테이블 구조 변경 시 증가해야 함)
        private const val DB_VERSION = 2
    }

    // DB 처음 생성 시 호출
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    // DB 버전 변경 시 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        createTables(db)
    }

    // 테이블 누락 방지용 -> DB 파일이 이미 존재해도 앱 실행 시마다 호출
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        createTables(db)
    }

    // 오답 테이블 생성
    // slang_wrong: 신조어 오답
    // spelling_wrong: 맞춤법 오답
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

    // 신조어 오답 (날짜별 그룹화 + 중복 제거)
    fun getSlangWrongGrouped(
        context: Context,
        slangDB: SlangDBManager
    ): List<WrongDateGroup> {

        val userId = UserManager.getUserId(context)
        val db = readableDatabase

        // 날짜별 오답 목록을 유지, 순서대로 저장
        val map = linkedMapOf<String, MutableList<WrongItem>>()

        // 같은 날 같은 문제를 여러 번 틀려도 1번만 표시
        // 가장 마지막에 틀린 시간 기준으로 정렬
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

            // 원본 퀴즈 정보 조회
            val quiz = slangDB.getQuizById(quizId)
            if (quiz != null) {
                map.getOrPut(date) { mutableListOf() }
                    .add(WrongItem(quizId, quiz.slangWord))
            }
        }

        cursor.close()
        db.close()

        // 날짜별 그룹 리스트로 변환
        return map.map { WrongDateGroup(it.key, it.value) }
    }

    // 맞춤법 오답 (날짜별 그룹화 + 중복 제거)
    fun getSpellingWrongGrouped(
        context: Context,
        dbManager: SpellDBManager
    ): List<WrongDateGroup> {

        val userId = UserManager.getUserId(context)
        val db = readableDatabase

        // 날짜별 오답 목록을 유지, 순서대로 저장
        val map = linkedMapOf<String, MutableList<WrongItem>>()

        // 같은 날 같은 문제를 여러 번 틀려도 1번만 표시
        // 가장 마지막에 틀린 시간 기준으로 정렬
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

            // 원본 퀴즈 정보 조회
            val quiz = dbManager.getQuizById(quizId)
            if (quiz != null) {
                map.getOrPut(date) { mutableListOf() }
                    .add(WrongItem(quizId, quiz.correct))
            }
        }

        cursor.close()
        db.close()

        // 날짜별 그룹을 리스트로 변환
        return map.map { WrongDateGroup(it.key, it.value) }
    }

    // 오답 저장 로직
    // 신조어 오답 저장
    fun saveSlangWrong(context: Context, quizId: Int, userAnswer: String) {

        // 현재 로그인힌 사용자 ID 조회 -> 여러 사용자의 오답을 구분하기 위해
        val userId = UserManager.getUserId(context)

        // 쓰기 가능한 DB 객체 획득
        val db = writableDatabase

        val values = ContentValues().apply {
            put("user_id", userId)  // 사용자 구분
            put("quiz_id", quizId)  // 틀린 문제 ID
            put("user_answer", userAnswer)  // 사용자가 선택/입력한 답
            put("wrong_time", System.currentTimeMillis() / 1000)  // 오답 시각 (초 단위)
        }

        // 테이블에 오답 1개 저장
        db.insert("slang_wrong", null, values)
        db.close()
    }

    // 맞춤법 오답 저장
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

    // 현재 사용자 오답 전체 삭제
    // 오답 노트 초기화 버튼 클릭 시 호출됨
    // 다른 사용자 데이터는 삭제되지 않도록
    // user_id 조건 포함해야 함
    fun clearAllWrong(context: Context) {

        // 현재 사용자 ID
        val userId = UserManager.getUserId(context)

        // 쓰기 가능한 DB 객체
        val db = writableDatabase

        // user_id에 해당하는 데이터만 삭제
        db.execSQL("DELETE FROM slang_wrong WHERE user_id = ?", arrayOf(userId))
        db.execSQL("DELETE FROM spelling_wrong WHERE user_id = ?", arrayOf(userId))
        db.close()
    }
}