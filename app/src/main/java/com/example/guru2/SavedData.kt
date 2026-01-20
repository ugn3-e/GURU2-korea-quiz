package com.example.guru2

// DB에서 가져오기
data class QuizData(
    val id: Int,
    val sentence: String,
    val correct: String,
    val correct_exp: String? = null,
    val incorrect_exp: String? = null,
    val source: String? = null,
    val saved_date: String? = null,
    val is_saved: Int = 0
)

// 날짜별로 묶어서 세로 리스트에 보낼 정보
data class DailySection(
    val date: String,
    val quizzes: List<QuizData>
)