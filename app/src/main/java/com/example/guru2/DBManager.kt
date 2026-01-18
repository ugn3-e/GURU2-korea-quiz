package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DBManager(
    val context: Context
) : SQLiteOpenHelper(context, "spelling_quiz.db", null, 2) {

    init {
        copyDatabase() // ☑️ DB 파일 없으면 복사해오기
    }

    // ☑️ 추가
    private fun copyDatabase() {
        val dbPath = context.getDatabasePath("spelling_quiz.db")
        if (!dbPath.exists()) {
            try {
                val inputStream = context.assets.open("spelling_quiz.db")
                val outputStream = FileOutputStream(dbPath)
                val buffer = ByteArray(1024)
                var length: Int
                while(inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // ☑️ 파일에 테이블 있으니 비워두기 (없앰)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
//        db!!.execSQL("DROP TABLE IF EXISTS spelling_quiz")
//        onCreate(db)
        // 업데이트 필요하면 파일 다시 복사하거나 로직 추가
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