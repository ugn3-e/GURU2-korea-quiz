package com.example.guru2

// 날짜 그룹
data class WrongDateGroup(
    val date: String,
    val items: List<WrongItem>
)

// 가로 카드 하나
data class WrongItem(
    val quizId: Int,
    val displayText: String // slang_word or sentence
)
