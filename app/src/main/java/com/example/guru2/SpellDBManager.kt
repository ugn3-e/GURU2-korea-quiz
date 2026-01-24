package com.example.guru2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class SpellDBManager(
    val context: Context
) : SQLiteOpenHelper(context, "spelling_quiz.db", null, 2) {

    // 가장 먼저 실행됨
    init {
        copyDatabase() // DB 파일 없으면 복사해오기
    }

    private fun copyDatabase() {
        val dbPath = context.getDatabasePath("spelling_quiz.db") // DB 경로

        // 파일 없으면
        if (!dbPath.exists()) {
            try {
                val inputStream = context.assets.open("spelling_quiz.db")

                // assets에 있는 db 경로로 복사
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
        // 파일에 테이블 있으니 비워두기
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }

    // 한 문제만 가져오기
    fun getQuizById(id: Int) : QuizData? {
        val db = this.readableDatabase

        // id가 입력값과 같은 행을 선택
        var cursor = db.rawQuery("SELECT * FROM spelling_quiz WHERE id = ?", arrayOf(id.toString()))

        var quiz: QuizData? = null
        if(cursor.moveToFirst()) {
            quiz = QuizData( // // 각 컬럼 값들을 꺼내서 QuizData 객체로 만들기
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")),
                source = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")),
                image_path = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
            )
        }
        cursor.close() // 데이터 탐색 닫기
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
                    saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")),
                    image_path = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 콘텐츠 저장한 목록
    fun getSavedQuizzes(): List<QuizData> {
        val list = mutableListOf<QuizData>()
        val db = this.readableDatabase
        // is_saved가 1인 데이터만 최신으로(id 역순) 가져옴
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
                    incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")),
                    image_path = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 콘텐츠 저장하기
    fun saveQuizContent(id: Int, date: String) {
        val db = this.writableDatabase
        val values = android.content.ContentValues().apply {
            put("is_saved", 1)       // 저장 상태 1로 변경
            put("saved_date", date)  // 현재 날짜 저장
        }

        val rows = db.update("spelling_quiz", values, "id = ?", arrayOf(id.toString()))
        android.util.Log.d("DB_CHECK", "수정된 행 개수: $rows (id: $id)")
        db.close()
    }
}