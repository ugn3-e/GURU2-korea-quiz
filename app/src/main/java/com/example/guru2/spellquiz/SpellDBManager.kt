package com.example.guru2.spellquiz

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.guru2.saved.QuizData
import java.io.FileOutputStream

class SpellDBManager(
    val context: Context
) : SQLiteOpenHelper(context, "spelling_quiz.db", null, 2) {

    // 가장 먼저 실행됨
    init {
        copyDatabase() // DB 파일 없으면 복사해오기
    }

    private fun copyDatabase() {
        val dbPath = context.getDatabasePath("spelling_quiz.db") // 앱 내부의 DB 저장 경로 추출

        // 해당 경로에 DB 파일이 없을 경우
        if (!dbPath.exists()) {
            try {
                // assets 폴더에서 원본 DB 파일 읽기
                val inputStream = context.assets.open("spelling_quiz.db")

                // assets에 있는 db 경로로 복사
                val outputStream = FileOutputStream(dbPath)

                // 1KB 단위로 데이터 복사
                val buffer = ByteArray(1024)
                var length: Int
                while(inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                // 스트림 닫기 및 버퍼 비우기
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                // 파일 복사 중 오류 발생 시 로그 출력
                e.printStackTrace()
            }
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        // 이미 파일에 테이블 있으니 비워두기
    }

    // DB 버전이 변경될 때 수행할 로직
    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {

    }

    // 한 문제만 가져오기
    fun getQuizById(id: Int) : QuizData? {
        val db = this.readableDatabase // 읽기 전용 DB 연결

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


    // DB에 저장된 모든 퀴즈 목록 반환
    fun getAllQuizzed(): List<QuizData> {
        val list = mutableListOf<QuizData>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM spelling_quiz", null)

        // 커서를 처음부터 끝까지 이동하며 리스트에 담기
        if(cursor.moveToFirst()) {
            do {
                list.add(
                    QuizData(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                        correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                        correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                        incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")),
                        source = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                        saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")),
                        image_path = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 콘텐츠 저장한 퀴즈 목록 가져오기
    fun getSavedQuizzes(): List<QuizData> {
        val list = mutableListOf<QuizData>()
        val db = this.readableDatabase

        // is_saved가 1인 데이터만 최신으로(id 역순) 가져옴
        val cursor = db.rawQuery("SELECT * FROM spelling_quiz WHERE is_saved = 1 ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    QuizData(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        sentence = cursor.getString(cursor.getColumnIndexOrThrow("sentence")),
                        correct = cursor.getString(cursor.getColumnIndexOrThrow("correct")),
                        source = cursor.getString(cursor.getColumnIndexOrThrow("source")),
                        saved_date = cursor.getString(cursor.getColumnIndexOrThrow("saved_date")),
                        correct_exp = cursor.getString(cursor.getColumnIndexOrThrow("correct_exp")),
                        incorrect_exp = cursor.getString(cursor.getColumnIndexOrThrow("incorrect_exp")),
                        image_path = cursor.getString(cursor.getColumnIndexOrThrow("image_path"))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // 콘텐츠를 저장 상태로 업데이트
    fun saveQuizContent(id: Int, date: String) {
        val db = this.writableDatabase

        // 업데이트할 데이터를 key-value 쌍으로 구성
        val values = ContentValues().apply {
            put("is_saved", 1)       // 저장 상태 1로 변경
            put("saved_date", date)  // 현재 날짜 저장
        }

        // 해당 ID의 행을 찾아 업데이트 실행
        val rows = db.update("spelling_quiz", values, "id = ?", arrayOf(id.toString()))
        Log.d("DB_CHECK", "수정된 행 개수: $rows (id: $id)")
        db.close() // 작업 완료 후 DB 닫기
    }
}