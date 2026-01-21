package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class SlangDBManager(private val context: Context)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // 디미 유진_DB 파일 이름
        private const val DB_NAME = "slang_quiz.db"
        // 디미 유진_DB 파일 버전
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 디미 유진_assets 폴더에서 이미 완성된 DB 불러와 사용 -> 여기서 테이블 생성 안 함
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    // 디미 유진_assets 폴더 -> 내부 DB 복사 (이미 존재하면 다시 복사 하지 않음)
    private fun copyDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(DB_NAME)

        // 디미 유진_DB 파일 존재하지 않을 경우
        if (!dbFile.exists()) {
            dbFile.parentFile?.mkdirs()

            context.assets.open(DB_NAME).use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    // 디미 유진_DB 열기
    private fun openDatabase(): SQLiteDatabase {
        copyDatabaseIfNeeded()
        return SQLiteDatabase.openDatabase(
            context.getDatabasePath(DB_NAME).path,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }

    // 디미 유진_신조어 문제 1개 가져오기
    fun getQuizById(id: Int): SlangQuizData? {
        val db = openDatabase()

        val cursor = db.rawQuery(
            """
            SELECT id, question, choice1, choice2, choice3, choice4,
                   answer, example_image, explanation, notice, slang_word
            FROM slang_quiz
            WHERE id = ?
            """.trimIndent(),
            arrayOf(id.toString())
        )

        var quiz: SlangQuizData? = null

        // 디미 유진_결과가 있으면 SlangQuizData 객체로 변환
        if (cursor.moveToFirst()) {
            quiz = SlangQuizData(
                id = cursor.getInt(0),
                question = cursor.getString(1),
                choice1 = cursor.getString(2),
                choice2 = cursor.getString(3),
                choice3 = cursor.getString(4),
                choice4 = cursor.getString(5),
                answer = cursor.getString(6),
                exampleImage = cursor.getString(7),
                explanation = cursor.getString(8),
                notice = cursor.getString(9),
                slangWord = cursor.getString(10)
            )
        }

        cursor.close()
        db.close()
        return quiz
    }
}


//디미 유진_SlangQuizData
// slang_quiz 테이블의 1행(문제 1개)을 표현
data class SlangQuizData(
    val id: Int,
    val question: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String,
    val answer: String,
    val exampleImage: String,
    val explanation: String,
    val notice: String,
    val slangWord: String
)