package com.example.guru2.util

object LevelUtil {
// 사용자가 푼 문제 수 → 레벨 계산
    fun calculateLevel(solvedCount: Int): Int {
        return when {
            solvedCount >= 60 -> 7
            solvedCount >= 50 -> 6
            solvedCount >= 40 -> 5
            solvedCount >= 30 -> 4
            solvedCount >= 20 -> 3
            solvedCount >= 10 -> 2
            else -> 1
        }
    }
}
