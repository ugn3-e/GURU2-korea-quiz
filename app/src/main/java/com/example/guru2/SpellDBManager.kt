package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class SpellDBManager(
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
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")), // id 추가
                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")), // 괄호 정리
                source = cursor.getString(cursor.getColumnIndexOrThrow("source")), // source 추가
                saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")) // saved_date 추가
            )
        }
        cursor.close()
        return quiz
    }

    fun getAllQuizzed(): List<QuizData> {
        val list = mutableListOf<QuizData>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM spelling_quiz", null)

        if(cursor.moveToFirst()) {
            do {
                list.add(QuizData(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                    correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                    correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                    incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")),
                    source = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                    saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getSavedQuizzes(): List<QuizData> {
        val list = mutableListOf<QuizData>()
        val db = this.readableDatabase
        // is_saved가 1인 데이터만 최신순으로 가져옵니다.
        val cursor = db.rawQuery("SELECT * FROM spelling_quiz WHERE is_saved = 1 ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(QuizData(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                    correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                    source = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                    saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")),
                    correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                    incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun saveQuizContent(id: Int, date: String) {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("is_saved", 1)       // 저장 상태 1로 변경
            put("saved_date", date)  // 현재 날짜 저장
        }
        // 해당 id를 가진 행만 업데이트
        //db.update("spelling_quiz", values, "id = ?", arrayOf(id.toString()))
        val rows = db.update("spelling_quiz", values, "id = ?", arrayOf(id.toString()))
        android.util.Log.d("DB_CHECK", "수정된 행 개수: $rows (id: $id)")
        db.close()
    }
}