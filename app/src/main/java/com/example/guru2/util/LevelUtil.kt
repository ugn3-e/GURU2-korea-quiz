package com.example.guru2.util

object LevelUtil {

    fun calculateLevel(solvedCount: Int): Int {
        return when {
            solvedCount >= 60 -> 5
            solvedCount >= 30 -> 4
            solvedCount >= 20 -> 3
            solvedCount >= 10 -> 2
            else -> 1
        }
    }
}
