package com.example.guru2

// 날짜별 오답 그룹 데이터
// 세로 RecyclerView 한 칸(하루)에 해당

// 디미 유진_날짜 그룹
data class WrongDateGroup(
    val date: String,  // 오답 발생 날짜
    val items: List<WrongItem>  // 해당 날짜에 틀린 오답 문제 목록 -> 가로 RecyclerView에서 사용
)

// 디미 유진_가로 RecyclerView에 표시되는 오답 카드 하나
data class WrongItem(
    val quizId: Int,  // 원본 문제 DB 조회용 문제 ID
    val displayText: String  // 화면에 표시할 텍스트 (신조어: slangWord / 맞춤법: correct)
)
